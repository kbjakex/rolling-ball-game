package rollingball.functions;

import java.util.Arrays;

public final class FunctionParser {
    private FunctionParser() {
    }

    public static Function parse(String exprString, String conditionString) {
        var expr = parseChecked(exprString, new ExpressionParser());
        var cond = parseChecked(conditionString, new ConditionParser());

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
