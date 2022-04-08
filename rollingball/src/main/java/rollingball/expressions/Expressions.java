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

    public enum Op {
        ADD(1), SUB(1), MUL(2), DIV(2);

        private final int precedence;
        private Op(int precedence) {
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

    @FunctionalInterface
    public interface Expr {
        double evaluate(EvalContext ctx);
        
        default Double tryConstEvaluate() {
            return null;
        }

        static Expr constant(double val) {
            return new Expr() {
                @Override
                public double evaluate(EvalContext ctx) {
                    return val;
                }
                
                @Override
                public Double tryConstEvaluate() {
                    return val;
                }
            };
        }
    }
}
