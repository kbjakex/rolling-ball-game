package rollingball.functions;

/**
 * Represents a mathematical expression filtered by a conditional expression.
 */
public final class Function {

    /**
     * Represents a mathematical expression. Expressions typically comprise of
     * three building blocks: binary operations, unary operations and values.
     * Values may be constants, variables or function calls. This interface
     * serves as the base class for all of these.
     */
    @FunctionalInterface
    public interface Expr {
        /**
         * Evaluates the expression. For expressions returned by
         * {@link ExpressionParser}, this should always succeed.
         * @param ctx the context in which to evaluate the expression.
         * @return the result of evaluating the expression.
         */
        double eval(EvalContext ctx);

        /**
         * Tries to to evaluate the expression without context. Meant for
         * simplifying expressions where possible during parsing.
         * @returns the result of evaluating the expression, or null if context is required.
         */
        default Double tryConstEvaluate() {
            return null;
        }

        /**
         * Creates an expression with a constant value. Unlike
         * <code>expr -> val</code>, this implementation will correctly
         * evaluate to the number without context, making it
         * possible to simplify expressions.
         */
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

    /**
     * Represents a conditional expression. This differs from
     * {@link Expr} only in that this evaluates to a boolean value,
     * not a double.
     */
    @FunctionalInterface
    public interface Condition {
        /**
         * Evaluates the condition. For conditions returned by
         * {@link ConditionParser}, this should always succeed.
         * @param ctx the context in which to evaluate the condition.
         * @return the result of evaluating the condition.
         */
        boolean eval(EvalContext ctx);

        /**
         * Tries to to evaluate the condition without context. Meant for
         * simplifying conditions where possible during parsing.
         * @returns the result of evaluating the condition, or null if context is required.
         */
        default Boolean tryConstEvaluate() {
            return null;
        }

        /**
         * Creates a condition with a constant value. Unlike
         * <code>cond -> val</code>, this implementation will correctly
         * evaluate to the boolean without context, making it
         * possible to simplify conditions.
         */
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

    /**
     * Creates a new function with the given expression and condition.
     * @param formula the expression to evaluate.
     * @param condition the condition to evaluate.
     */
    public Function(Expr formula, Condition condition) {
        this.formula = formula;
        this.condition = condition;
    }

    /**
     * Tests if the condition permits evaluating the function
     * with the given context.
     * @param ctx the context in which to evaluate the condition.
     * @return true if eval() should be called with this context.
     */
    public boolean canEval(EvalContext ctx) {
        return this.condition.eval(ctx);
    }

    /**
     * Evaluates the function with the given context.
     * {@link canEval(EvalContext)} should be checked first for intended results.
     * @param ctx the context in which to evaluate the expression.
     * @return the result of evaluating the expression.
     */
    public double eval(EvalContext ctx) {
        return this.formula.eval(ctx);
    }

    /**
     * Evaluates the function with the given context, at the specified
     * x coordinate. This is a convenience method that calls
     * {@link eval(EvalContext)}.
     * @param x the x coordinate to evaluate the function at.
     * @param ctx the context in which to evaluate the expression.
     * @return the result of evaluating the expression.
     */
    public double evalAt(double x, EvalContext ctx) {
        ctx.x = x;
        return eval(ctx);
    }
}
