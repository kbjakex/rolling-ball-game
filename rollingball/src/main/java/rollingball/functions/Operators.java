package rollingball.functions;

public final class Operators {
    private Operators() {
    } // Make non-instantiable

    public enum ArithmeticOp {
        ADD(1), SUB(1), MUL(2), DIV(2), POW(3);

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
                case POW -> Math.pow(lhs, rhs);
            };
        }
    }

    public enum RelationalOp {
        LT, LE, GT, GE
    }
}
