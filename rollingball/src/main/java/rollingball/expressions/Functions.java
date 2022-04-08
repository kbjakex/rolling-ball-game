package rollingball.expressions;

import java.util.function.Supplier;

import rollingball.expressions.ExpressionParser.ParserException;
import rollingball.expressions.Expressions.Expr;

public final class Functions {
    private Functions() {
    } // Make non-instantiable

    public static Expr parseFunctionCall(String name, Expr firstParam, Supplier<Expr> paramSupplier) {
        Expr result;
        if ((result = tryParseTrigonometric(name, firstParam)) != null) {
            return result;
        }
        if ((result = tryParseMiscellaneous(name, firstParam)) != null) {
            return result;
        }
        if ((result = tryParseMultiParam(name, firstParam, paramSupplier.get())) != null) {
            return result;
        }

        throw new ParserException("Unknown function: '" + name + "'");
    }

    private static Expr tryParseTrigonometric(String name, Expr param) {
        return switch (name.toLowerCase()) {
            case "sin" -> ctx -> Math.sin(param.evaluate(ctx));
            case "cos" -> ctx -> Math.cos(param.evaluate(ctx));
            case "tan" -> ctx -> Math.tan(param.evaluate(ctx));
            case "asin", "arcsin" -> ctx -> Math.asin(param.evaluate(ctx));
            case "acos", "arccos" -> ctx -> Math.acos(param.evaluate(ctx));
            case "atan", "arctan" -> ctx -> Math.atan(param.evaluate(ctx));
            case "sinh" -> ctx -> Math.sinh(param.evaluate(ctx));
            case "cosh" -> ctx -> Math.cosh(param.evaluate(ctx));
            case "tanh" -> ctx -> Math.tanh(param.evaluate(ctx));
            default -> null;
        };
    }

    private static Expr tryParseMiscellaneous(String name, Expr param) {
        return switch (name.toLowerCase()) {
            case "exp" -> ctx -> Math.exp(param.evaluate(ctx));
            case "log", "ln" -> ctx -> Math.log(param.evaluate(ctx));
            case "log10", "lg" -> ctx -> Math.log10(param.evaluate(ctx));
            case "sqrt" -> ctx -> Math.sqrt(param.evaluate(ctx));
            case "cbrt" -> ctx -> Math.cbrt(param.evaluate(ctx));
            case "abs" -> ctx -> Math.abs(param.evaluate(ctx));
            case "floor" -> ctx -> Math.floor(param.evaluate(ctx));
            case "ceil" -> ctx -> Math.ceil(param.evaluate(ctx));
            case "round" -> ctx -> Math.round(param.evaluate(ctx));
            case "sign", "signum" -> ctx -> Math.signum(param.evaluate(ctx));
            default -> null;
        };
    }

    private static Expr tryParseMultiParam(String name, Expr param1, Expr param2) {
        return switch (name) {
            case "min" -> ctx -> Math.min(param1.evaluate(ctx), param2.evaluate(ctx));
            case "max" -> ctx -> Math.max(param1.evaluate(ctx), param2.evaluate(ctx));
            case "pow" -> ctx -> Math.pow(param1.evaluate(ctx), param2.evaluate(ctx));
            case "atan2" -> ctx -> Math.atan2(param1.evaluate(ctx), param2.evaluate(ctx));
            case "hypot" -> ctx -> {
                var x1 = param1.evaluate(ctx);
                var x2 = param2.evaluate(ctx);
                return Math.sqrt(x1 * x1 + x2 * x2);
            };
            default -> null;
        };
    }
}
