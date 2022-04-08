package rollingball.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import rollingball.expressions.Expressions.Expr;
import rollingball.expressions.Expressions.Op;

public final class ExpressionParser {
    public static final class ParserException extends RuntimeException {
        public ParserException(String message) {
            super(message);
        }

        public ParserException(String format, Object... args) {
            this(String.format(format, args));
        }
    }

    private final char[] src;
    private int srcPos;

    private ExpressionParser(char[] src) {
        this.src = src;
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
            throw new ParserException("Expected a number, found '%s'", srcPos == src.length ? "EOF" : ("" + src[srcPos]));
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

        var result = Functions.parseFunctionCall(name, firstParam, paramSupplier);
        expect(')', "Missing closing ')'");
        return result;
    }

    private Expr parseVariableOrFuncCall() {
        var name = parseIdentifier();
        if (consume('(')) {
            return parseFuncCall(name);
        }

        return switch (name) {
            case "x" -> ctx -> ctx.varX;
            case "t" -> ctx -> ctx.varT;
            case "pi", "PI" -> Expr.constant(Math.PI);
            case "e", "E" -> Expr.constant(Math.E);
            default ->
                throw new ParserException("Unknown variable '%s', only 'x', 't', 'pi' and 'e' are allowed", name);
        };
    }

    private Expr parseOperandInner() {
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

    private Expr parseOperand() {
        var negate = consume('-');
        var result = parseOperandInner();

        if (negate) {
            return ctx -> -result.evaluate(ctx);
        } else {
            return result;
        }
    }

    private Op tryParseOperator() {
        if (!hasNext()) {
            return null;
        }

        // Implicit multiplication sign before identifiers, e.g. `5x`, `3sin(x)`, `3x^2`
        // etc.
        if (Character.isAlphabetic(src[srcPos])) {
            return Op.MUL;
        }

        return switch (src[srcPos++]) {
            case '+' -> Op.ADD;
            case '-' -> Op.SUB;
            case '*' -> Op.MUL;
            case '/' -> Op.DIV;
            default -> {
                srcPos--;
                yield null;
            }
        };
    }

    private Expr parseComplexExpr(Expr firstOperand, Op firstOp) {
        var operatorStack = new ArrayList<Op>();
        operatorStack.add(firstOp);

        var operandStack = new ArrayList<Expr>();
        operandStack.add(firstOperand);
        operandStack.add(parseOperand());

        Op op;
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

    private void mergeTopOfStack(List<Expr> operandStack, List<Op> operatorStack) {
        var mergedOp = operatorStack.remove(operatorStack.size() - 1);
        var rhs = operandStack.remove(operandStack.size() - 1);
        var lhs = operandStack.remove(operandStack.size() - 1);

        var lhsConstEval = lhs.tryConstEvaluate();
        var rhsConstEval = rhs.tryConstEvaluate();
        if (lhsConstEval != null && rhsConstEval != null) {
            operandStack.add(Expr.constant(mergedOp.apply(lhsConstEval, rhsConstEval)));
        } else {
            Expr expr = switch (mergedOp) {
                case ADD -> ctx -> lhs.evaluate(ctx) + rhs.evaluate(ctx);
                case SUB -> ctx -> lhs.evaluate(ctx) - rhs.evaluate(ctx);
                case MUL -> ctx -> lhs.evaluate(ctx) * rhs.evaluate(ctx);
                case DIV -> ctx -> lhs.evaluate(ctx) / rhs.evaluate(ctx);
            };
            operandStack.add(expr);
        }
    }

    private void expect(char type, String format, Object... args) {
        if (!consume(type)) {
            throw new ParserException(format, args);
        }
    }

    private boolean consume(char type) {
        if (nextIs(type)) {
            srcPos += 1;
            return true;
        }
        return false;
    }

    private boolean nextIs(char type) {
        return srcPos < src.length && src[srcPos] == type;
    }

    private boolean hasNext() {
        return srcPos < src.length;
    }

    public Expr parse() {
        return parseExpr();
    }

    public static Expr parse(String expression) {
        var dense = removeWhitespace(expression);
        if (dense.length == 0) {
            return null;
        }

        var parser = new ExpressionParser(dense);
        var result = parser.parse();
        if (parser.srcPos != dense.length) {
            throw new ParserException("Trailing content: '%s'",
                    new String(dense, parser.srcPos, dense.length - parser.srcPos));
        }

        return result;
    }

    public static char[] removeWhitespace(String expr) {
        var result = new char[expr.length()];
        var i = 0;
        for (var c : expr.toCharArray()) {
            if (Character.isWhitespace(c)) {
                continue;
            }
            result[i++] = c;
        }
        return Arrays.copyOfRange(result, 0, i);
    }

}
