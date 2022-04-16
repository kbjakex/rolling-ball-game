package rollingball.expressions;

public final class Expressions {
    private Expressions() {} // Make non-instantiable

    public static final class EvalContext {
        public final double varT;
        public double varX;

        public EvalContext(double varT) {
            this.varT = varT;
        }
    }

    public enum ArithmeticOp {
        ADD(1), SUB(1), MUL(2), DIV(2);

        private final int precedence;
        private ArithmeticOp(int precedence) {
            this.precedence = precedence;
        }

        public int getPrecedence() {
            return this.precedence;
        }

        public double apply(double lhs, double rhs) {
            return switch (this) {
                case ADD -> lhs + rhs;
                case SUB -> lhs - rhs;
                case MUL -> lhs * rhs;
                case DIV -> lhs / rhs;
            };
        }
    }

    public enum RelationalOp {
        LT(1), LE(1), GT(1), GE(1), EQ(0), NE(0);

        private final int precedence;
        private RelationalOp(int precedence) {
            this.precedence = precedence;
        }

        public int getPrecedence() {
            return this.precedence;
        }

        public boolean apply(double lhs, double rhs) {
            return switch (this) {
                case LT -> lhs < rhs;
                case LE -> lhs <= rhs;
                case GT -> lhs > rhs;
                case GE -> lhs >= rhs;
                case EQ -> lhs == rhs;
                case NE -> lhs != rhs;
            };
        }
    }

    public enum BooleanOp {
        AND(2), OR(1), NOT(3);

        private final int precedence;
        private BooleanOp(int precedence) {
            this.precedence = precedence;
        }

        public int getPrecedence() {
            return this.precedence;
        }

        public boolean apply(boolean lhs, boolean rhs) {
            return switch (this) {
                case AND -> lhs && rhs;
                case OR -> lhs || rhs;
                case NOT -> !lhs;
            };
        }
    }

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

    public static final class Function {
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
    }
}
