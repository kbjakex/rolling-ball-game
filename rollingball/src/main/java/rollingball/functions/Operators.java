package rollingball.functions;

/**
 * Namespace for operator enums.
 */
public final class Operators {
    private Operators() {
    } // Make non-instantiable

    /**
     * Represents an arithmetic operator.
     */
    public enum ArithmeticOp {
        /** 
         * Represents the addition operator '+'.
         */
        ADD(1), 
        /** 
         * Represents the subtraction operator '-'.
         */
        SUB(1), 
        /** 
         * Represents the multiplication operator '*'.
         */
        MUL(2), 
        /** 
         * Represents the division operator '/'.
         */
        DIV(2), 
        /** 
         * Represents the exponentiation operator '^'.
         */
        POW(3);

        private final int precedence;

        private ArithmeticOp(int precedence) {
            this.precedence = precedence;
        }

        /**
         * Gets the precedence of this operator. A higher precedence means that this operator
         * will be evaluated before other operators with a lower precedence.
         * @return the precedence of this operator.
         */
        public int getPrecedence() {
            return this.precedence;
        }

        /**
         * Applies this operator to the two inputs.
         * @param lhs the left-hand side operand.
         * @param rhs the right-hand side operand.
         * @return the result of applying this operator to the inputs.
         */
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

    /**
     * Represents a comparison operator.
     * 
     * Implementation note: these operators all have the same precedence, which is
     * why there is currently no precedence field.
     */
    public enum RelationalOp {
        /** 
         * Represents the less-than operator '&lt;'.
         */
        LT, 
        /** 
         * Represents the less-than-or-equal operator '&lt;='.
         */
        LE, 
        /** 
         * Represents the greater-than operator '&gt;'.
         */
        GT, 
        /** 
         * Represents the greater-than-or-equal operator '&gt;='.
         */
        GE
    }
}
