package rollingball.functions;

public final class Operators {
    private Operators() {} // Make non-instantiable

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
}
