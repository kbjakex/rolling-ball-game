package rollingball.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        var isNegative = !consume('+') && consume('-');

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

        if (isNegative) {
            result = -result;
        }

        if (posBefore == srcPos) {
            throw new ParserException("Expected a number, found '%s'", srcPos == src.length ? "EOF" : ("" + src[srcPos]));
        }

        return Expr.constant(result);
    }

    private Expr parseOperand() {
        if (consume('(')) {
            var result = parseExpr();
            expect(')', "Missing closing ')'");
            return result;
        }

        return parseConstant();        
    }

    private Op tryParseOperator() {
        if (!hasNext()) return null;
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

    private Expr parseExpr() {
        var root = parseOperand();
        
        var op = tryParseOperator();
        if (op == null) return root;
        
        var operatorStack = new ArrayList<Op>();
        operatorStack.add(op);

        var operandStack = new ArrayList<Expr>();
        operandStack.add(root);
        operandStack.add(parseOperand());

        while ((op = tryParseOperator()) != null) {
            var operand = parseOperand();

            while (!operatorStack.isEmpty() && op.precedence <= operatorStack.get(operatorStack.size()-1).precedence) {
                mergeTopOfStack(operandStack, operatorStack);
            }

            operatorStack.add(op);
            operandStack.add(operand);
        }

        while (!operatorStack.isEmpty()) {
            mergeTopOfStack(operandStack, operatorStack);
        }

        if (!operatorStack.isEmpty()) throw new ParserException("Mismatched parentheses; found closing ')' but expression had %d unmatched operators", operatorStack.size());
        if (operandStack.size() > 1) throw new ParserException("Mismatched parentheses; found closing ')' but expression had %d unmatched operands", operandStack.size() - 1);

        return operandStack.get(0);
    }

    private void mergeTopOfStack(List<Expr> operandStack, List<Op> operatorStack) {
        var mergedOp = operatorStack.remove(operatorStack.size()-1);
        var rhs = operandStack.remove(operandStack.size()-1);
        var lhs = operandStack.remove(operandStack.size()-1);

        var lhsAsConstant = lhs.tryConstEvaluate();
        var rhsAsConstant = rhs.tryConstEvaluate();
        if (lhsAsConstant != null && rhsAsConstant != null) { // Auto-reduce when possible
            operandStack.add(Expr.constant(mergedOp.apply(lhsAsConstant, rhsAsConstant)));
        } else { 
            operandStack.add(Expr.binary(mergedOp, lhs, rhs));
        }
    }

    private void expect(char type, String format, Object... args) {
        if (!consume(type)) throw new ParserException(format, args);
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
        if (dense.length == 0) return null;

        return new ExpressionParser(dense).parse();
    }

    public static char[] removeWhitespace(String expr) {
        var result = new char[expr.length()];
        var i = 0;
        for (var c : expr.toCharArray()) {
            if (Character.isWhitespace(c)) continue;
            result[i++] = c;
        }
        return Arrays.copyOfRange(result, 0, i);
    }
    
}
