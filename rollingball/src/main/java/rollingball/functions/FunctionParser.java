package rollingball.functions;

import java.util.Arrays;

/**
 * Namespace for a high-level interface to expression parsing.
 */
public final class FunctionParser {
    private FunctionParser() {
    }

    /**
     * Parses an expression and a condition and returns a function representing them.
     * If either of the strings are empty, null is returned.
     * @param expression the expression to parse.
     * @param condition the condition to parse.
     * @return the function representing the expression and the condition, or null.
     * @throws ParserException if the expression or condition have invalid syntax.
     */
    public static Function parse(String exprString, String conditionString) {
        var expr = parseChecked(exprString, new ExpressionParser());
        var cond = parseChecked(conditionString, new ConditionParser());

        if (expr == null) {
            return null;
        }
        if (cond == null) {
            return new Function(expr, ctx -> true);
        }
        return new Function(expr, cond);
    }

    private static <T> T parseChecked(String input, Parser<T> parser) {
        var dense = removeWhitespace(input);
        if (dense.length == 0) {
            return null;
        }

        var exprResult = parser.parse(dense, 0);
        if (exprResult.nextCharIdx() != dense.length) {
            var idx = exprResult.nextCharIdx();
            throw new ParserException("Trailing content at end: '%s'", new String(dense, idx, dense.length - idx));
        }
        return exprResult.value();
    }

    /**
     * A helper method that removes any whitespace in the input string.
     * @param expr the input string.
     * @return the input string with whitespace removed, as a char array.
     */
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
