package rollingball.functions;

/**
 * Evaluation context for functions. This class contains
 * values of all the runtime variables that expressions and conditions
 * can refer to.
 */
public final class EvalContext {
    /// The time variable. In seconds, since the start of the simulation.
    public final double t;
    /// The x coordinate to evaluate the function at. Not the ball position!
    public double x;

    /**
     * Constructs a new evaluation context with the specified time variable.
     * @param t
     */
    public EvalContext(double t) {
        this.t = t;
    }
}
