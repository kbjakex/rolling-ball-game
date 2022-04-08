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
        if ((result = tryParseMisc(name, firstParam)) != null) {
            return result;
        }
        return parseMultiParamFunctionCall(name, firstParam, paramSupplier.get());
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

    private static Expr tryParseMisc(String name, Expr param) {
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

    private static Expr parseMultiParamFunctionCall(String name, Expr firstParam, Expr secondParam) {
        return switch (name) {
            case "min" -> ctx -> Math.min(firstParam.evaluate(ctx), secondParam.evaluate(ctx));
            case "max" -> ctx -> Math.max(firstParam.evaluate(ctx), secondParam.evaluate(ctx));
            case "pow" -> ctx -> Math.pow(firstParam.evaluate(ctx), secondParam.evaluate(ctx));
            case "atan2" -> ctx -> Math.atan2(firstParam.evaluate(ctx), secondParam.evaluate(ctx));
            case "hypot" -> ctx -> {
                var x1 = firstParam.evaluate(ctx);
                var x2 = secondParam.evaluate(ctx);
                return Math.sqrt(x1 * x1 + x2 * x2);
            };
            default -> throw new ParserException("Unknown function: '" + name + "'");
        };
    }
}
