package rollingball.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import rollingball.functions.Function.Expr;
import rollingball.functions.Operators.ArithmeticOp;

public final class ExpressionParser extends Parser<Expr> {

    @Override
    protected final Expr doParse() {
        return parseExpr();
    }

    private Expr parseConstant() {
        var posBefore = srcPos;
        var result = 0.0;

        while (srcPos < src.length && Character.isDigit(src[srcPos])) {
            result = result * 10 + (src[srcPos] - '0');
            srcPos++;
        }

        if (consume('.')) {
            var frac = 0.1;
            while (srcPos < src.length && Character.isDigit(src[srcPos])) {
                result += (src[srcPos] - '0') * frac;
                frac *= 0.1;
                srcPos++;
            }
        }

        if (posBefore == srcPos) {
            if (srcPos == src.length) {
                throw new ParserException("Expression cannot end with an operator or '('");
            } else {
                throw new ParserException("Expected a number or '(' instead of '%s'", new String(src, srcPos, src.length - srcPos));
            }
        }

        return Expr.constant(result);
    }

    private String parseIdentifier() {
        var posBefore = srcPos;
        while (srcPos < src.length && Character.isJavaIdentifierPart(src[srcPos])) {
            srcPos++;
        }

        return new String(src, posBefore, srcPos - posBefore);
    }

    private Expr parseFuncCall(String name) {
        var firstParam = parseExpr();
        Supplier<Expr> paramSupplier = () -> {
            expect(',', "Missing comma between function parameters");
            return parseExpr();
        };

        var result = BuiltinFunctions.parseFunctionCall(name, firstParam, paramSupplier);
        expect(')', "Missing closing ')'");
        return result;
    }

    private Expr parseVariableOrFuncCall() {
        var name = parseIdentifier();
        if (consume('(')) {
            return parseFuncCall(name);
        }

        return switch (name) {
            case "x" -> ctx -> ctx.x;
            case "t" -> ctx -> ctx.t;
            case "pi", "PI" -> Expr.constant(Math.PI);
            case "e", "E" -> Expr.constant(Math.E);
            default ->
                throw new ParserException("Unknown variable '%s', only 'x', 't', 'pi' and 'e' are allowed", name);
        };
    }

    private Expr parseOperandInner2() {
        if (consume('(')) {
            var result = parseExpr();
            expect(')', "Missing closing ')'");
            return result;
        }

        if (hasNext() && Character.isAlphabetic(src[srcPos])) {
            return parseVariableOrFuncCall();
        }

        return parseConstant();
    }

    private Expr parseOperandInner1() {
        var result = parseOperandInner2();

        if (consume('^')) {
            var power = parseOperand();
            return makeArithmetic(result, ArithmeticOp.POW, power);
        }
        return result;
    }

    private Expr parseOperand() {
        var negate = consume('-');
        var result = parseOperandInner1();

        if (negate) {
            return ctx -> -result.eval(ctx);
        }
        return result;
    }

    private ArithmeticOp tryParseOperator() {
        if (!hasNext()) {
            return null;
        }

        // Implicit multiplication sign before identifiers, e.g. `5x`, `3sin(x)`, `3x^2`
        // etc.
        if (Character.isAlphabetic(src[srcPos])) {
            return ArithmeticOp.MUL;
        }

        return switch (src[srcPos++]) {
            case '+' -> ArithmeticOp.ADD;
            case '-' -> ArithmeticOp.SUB;
            case '*' -> ArithmeticOp.MUL;
            case '/' -> ArithmeticOp.DIV;
            default -> {
                srcPos--;
                yield null;
            }
        };
    }

    private Expr parseComplexExpr(Expr firstOperand, ArithmeticOp firstOp) {
        var operatorStack = new ArrayList<ArithmeticOp>();
        operatorStack.add(firstOp);

        var operandStack = new ArrayList<Expr>();
        operandStack.add(firstOperand);
        operandStack.add(parseOperand());

        ArithmeticOp op;
        while ((op = tryParseOperator()) != null) {
            var operand = parseOperand();

            while (!operatorStack.isEmpty() && op.getPrecedence() <= operatorStack.get(operatorStack.size() - 1).getPrecedence()) {
                mergeTopOfStack(operandStack, operatorStack);
            }

            operatorStack.add(op);
            operandStack.add(operand);
        }

        while (!operatorStack.isEmpty()) {
            mergeTopOfStack(operandStack, operatorStack);
        }

        return operandStack.get(0);
    }

    private Expr parseExpr() {
        var root = parseOperand();

        var op = tryParseOperator();
        if (op != null) {
            return parseComplexExpr(root, op);
        }

        return root;
    }

    private void mergeTopOfStack(List<Expr> operandStack, List<ArithmeticOp> operatorStack) {
        var mergedOp = operatorStack.remove(operatorStack.size() - 1);
        var rhs = operandStack.remove(operandStack.size() - 1);
        var lhs = operandStack.remove(operandStack.size() - 1);

        operandStack.add(makeArithmetic(lhs, mergedOp, rhs));
    }

    private Expr makeArithmetic(Expr lhs, ArithmeticOp op, Expr rhs) {
        var lhsConstEval = lhs.tryConstEvaluate();
        var rhsConstEval = rhs.tryConstEvaluate();
        if (lhsConstEval != null && rhsConstEval != null) {
            return Expr.constant(op.apply(lhsConstEval, rhsConstEval));
        }

        return switch (op) {
            case ADD -> ctx -> lhs.eval(ctx) + rhs.eval(ctx);
            case SUB -> ctx -> lhs.eval(ctx) - rhs.eval(ctx);
            case MUL -> ctx -> lhs.eval(ctx) * rhs.eval(ctx);
            case DIV -> ctx -> lhs.eval(ctx) / rhs.eval(ctx);
            case POW -> ctx -> Math.pow(lhs.eval(ctx), rhs.eval(ctx));
        };
    }

}