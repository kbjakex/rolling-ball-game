package rollingball.expressions;

import java.util.function.Supplier;

import rollingball.expressions.ExpressionParser.ParserException;
import rollingball.expressions.Expressions.Expr;

public final class Functions {
    private Functions() {} // Make non-instantiable

    public static Expr parseFunctionCall(String name, Expr firstParam, Supplier<Expr> paramSupplier) {
        return switch (name.toLowerCase()) {
            case "sin" -> ctx -> Math.sin(firstParam.evaluate(ctx));
            case "cos" -> ctx -> Math.cos(firstParam.evaluate(ctx));
            case "tan" -> ctx -> Math.tan(firstParam.evaluate(ctx));
            case "asin" -> ctx -> Math.asin(firstParam.evaluate(ctx));
            case "acos" -> ctx -> Math.acos(firstParam.evaluate(ctx));
            case "atan" -> ctx -> Math.atan(firstParam.evaluate(ctx));
            case "sinh" -> ctx -> Math.sinh(firstParam.evaluate(ctx));
            case "cosh" -> ctx -> Math.cosh(firstParam.evaluate(ctx));
            case "tanh" -> ctx -> Math.tanh(firstParam.evaluate(ctx));
            case "exp" -> ctx -> Math.exp(firstParam.evaluate(ctx));
            case "log" -> ctx -> Math.log(firstParam.evaluate(ctx));
            case "log10" -> ctx -> Math.log10(firstParam.evaluate(ctx));
            case "sqrt" -> ctx -> Math.sqrt(firstParam.evaluate(ctx));
            case "cbrt" -> ctx -> Math.cbrt(firstParam.evaluate(ctx));
            case "abs" -> ctx -> Math.abs(firstParam.evaluate(ctx));
            case "floor" -> ctx -> Math.floor(firstParam.evaluate(ctx));
            case "ceil" -> ctx -> Math.ceil(firstParam.evaluate(ctx));
            case "round" -> ctx -> Math.round(firstParam.evaluate(ctx));
            case "sign", "signum" -> ctx -> Math.signum(firstParam.evaluate(ctx));
            case "min" -> {
                var secondParam = paramSupplier.get();
                yield ctx -> Math.min(firstParam.evaluate(ctx), secondParam.evaluate(ctx));
            }
            case "max" -> {
                var secondParam = paramSupplier.get();
                yield ctx -> Math.max(firstParam.evaluate(ctx), secondParam.evaluate(ctx));
            }
            case "pow" -> {
                var secondParam = paramSupplier.get();
                yield ctx -> Math.pow(firstParam.evaluate(ctx), secondParam.evaluate(ctx));
            }
            case "atan2" -> {
                var secondParam = paramSupplier.get();
                yield ctx -> Math.atan2(firstParam.evaluate(ctx), secondParam.evaluate(ctx));
            }
            case "hypot" -> {
                var secondParam = paramSupplier.get();
                yield ctx -> {
                    var x1 = firstParam.evaluate(ctx);
                    var x2 = secondParam.evaluate(ctx);
                    return Math.sqrt(x1*x1 + x2*x2);
                };
            }
            default -> throw new ParserException("Unknown function: " + name);
        };
    }
}
