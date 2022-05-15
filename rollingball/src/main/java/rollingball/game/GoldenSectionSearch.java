package rollingball.game;

import rollingball.functions.EvalContext;
import rollingball.functions.Function;

/**
 * An implementation of the golden section search algorithm.
 * 
 * Problem: For the ball to roll on *top* of the curves, simply computing its y (upwards) position as
 * `f(x) + r` (where `r` is the Ball.RADIUS of the ball) is not enough (unless for *perfectly* horizontal 
 * lines). It would clip the curves on any upwards or downwards slopes.
 * 
 * One solution is to compute `f(x) - g(x)` (where `g(x)` models the curve of
 * the bottom of the ball, i.e `g(x) = r-sqrt(r*r - x*x)`) for x in [ballX-r, ballX+r].
 * The roots of this are the intersection points of the ball and the curve, but more importantly,
 * its maximum value is exactly the amount the ball needs to be shifted up.
 * 
 * This is exactly what this class is for.
 * 
 * Finding the maximum value is yuck because there's obviously no closed-form solution,
 * but golden-section search gets there robustly and with reliable performance: the
 * number of times the curve function needs to be evaluated is constant (about 35-40 in practice
 * depending on ball Ball.RADIUS and tolerance).
 * 
 * A note about modality: the algorithm expects unimodal functions, but in practice, *which* maximum
 * it converges to doesn't matter as long as it truly is the highest point. So even though I can
 * make zero guarantees about what the user inputs (sin(x) would be multimodal), this is fine.
*/
public final class GoldenSectionSearch {
    private GoldenSectionSearch() { // make non-instantiable
    }

    // Constants for the golden-section search
    private static final double PHI = (1 + Math.sqrt(5.0)) / 2.0;
    private static final double TOLERANCE = 1e-6;
    private static final double BALL_DIAMETER = Ball.RADIUS * 2.0;

    // Number of steps required to achieve desired tolerance
    private static final int NUM_ITERATIONS = (int) (Math
            .ceil(Math.log(BALL_DIAMETER / TOLERANCE) / Math.log(PHI))) - 1;

    /**
     * Computes the Y position that the ball must be placed on in order not to clip
     * the curve.
     * 
     * @param f     the curve function to to compute the Y for.
     * @param ctx   the evaluation context containing variables other than x.
     * @param ballX the ball's x position.
     * @return the ball's minimum Y position.
     */
    public static double computeBallYOnCurve(Function f, EvalContext ctx, double ballX) {
        var h = BALL_DIAMETER;
        var a = ballX - Ball.RADIUS;
        var b = ballX + Ball.RADIUS;

        var c = a + h / (PHI * PHI);
        var d = a + h / PHI;

        var yc = evalAt(c, f, ctx, ballX);
        var yd = evalAt(d, f, ctx, ballX);

        for (var k = 0; k < NUM_ITERATIONS; k++) {
            if (yc < yd) {
                b = d;
                d = c;
                yd = yc;
                h = h / PHI;
                c = a + h / (PHI * PHI);

                yc = evalAt(c, f, ctx, ballX);
            } else {
                a = c;
                c = d;
                yc = yd;
                h = h / PHI;
                d = a + h / PHI;

                yd = evalAt(d, f, ctx, ballX);
            }
        }

        // The algorithm obviously gives an interval rather than a single point. With a
        // low-enough tolerance, the midpoint of the interval should be a very good approximation.
        ctx.x = (yc < yd) ? (a + d) / 2.0 : (c + b) / 2.0;
        return -evalAt(ctx.x, f, ctx, ballX);
    }

    private static double evalAt(double x, Function fn, EvalContext ctx, double ballX) {
        return ballCurve(x - ballX) - fn.evalAt(x, ctx);
    }

    private static double ballCurve(double x) {
        return Ball.RADIUS - Math.sqrt(Ball.RADIUS * Ball.RADIUS - x * x);
    }
}
