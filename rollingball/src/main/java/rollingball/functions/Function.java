package rollingball.functions;

public final class Function {
    @FunctionalInterface
    public interface Expr {
        double eval(EvalContext ctx);

        default Double tryConstEvaluate() {
            return null;
        }

        static Expr constant(double val) {
            return new Expr() {
                @Override
                public double eval(EvalContext ctx) {
                    return val;
                }

                @Override
                public Double tryConstEvaluate() {
                    return val;
                }
            };
        }
    }

    @FunctionalInterface
    public interface Condition {
        boolean eval(EvalContext ctx);

        default Boolean tryConstEvaluate() {
            return null;
        }

        static Condition constant(boolean result) {
            return new Condition() {
                @Override
                public boolean eval(EvalContext ctx) {
                    return result;
                }

                @Override
                public Boolean tryConstEvaluate() {
                    return result;
                }
            };
        }
    }

    private final Expr formula;
    private final Condition condition;

    public Function(Expr formula, Condition condition) {
        this.formula = formula;
        this.condition = condition;
    }

    public boolean canEval(EvalContext ctx) {
        return this.condition.eval(ctx);
    }

    public double eval(EvalContext ctx) {
        return this.formula.eval(ctx);
    }

    /// Convenience method
    public double evalAt(double x, EvalContext ctx) {
        ctx.x = x;
        return eval(ctx);
    }
}
