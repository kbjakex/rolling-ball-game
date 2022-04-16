package rollingball.gamestate;

import rollingball.expressions.Expressions.EvalContext;
import rollingball.expressions.Expressions.Expr;

/*
Problem: For the ball to roll on *top* of the curves, simply computing its y (upwards) position as
`f(x) + r` (where `r` is the radius of the ball) is not enough (unless for *perfectly* horizontal 
lines). It would clip the curves on any upwards or downwards slopes.

One solution is to compute `f(x) - g(x)` (where `g(x)` models the curve of
the bottom of the ball, i.e `g(x) = r-sqrt(r*r - x*x)`) for x in [ballX-r, ballX+r].
The roots of this are the intersection points of the ball and the curve, but more importantly,
its maximum value is exactly the amount the ball needs to be shifted up.

This is exactly what this class is for.

Finding the maximum value is yuck because there's obviously no closed-form solution,
but golden-section search gets there robustly and with reliable performance: the
number of times the curve function needs to be evaluated is constant (about 35-40 in practice
depending on ball radius and tolerance).

A note about modality: the algorithm expects unimodal functions, but in practice, *which* maximum
it converges to doesn't matter as long as it truly is the highest point. So even though I can
make zero guarantees about what the user inputs (sin(x) would be multimodal), this is fine.
*/
public final class GoldenSectionSearch {
    private GoldenSectionSearch() {
    }

    // (should this be here?)
    public static final double BALL_RADIUS = 0.4;

    private static final double INV_PHI = (Math.sqrt(5.0) - 1.0) / 2.0; // 1 / phi

    private static final double INV_PHI_E2 = (3.0 - Math.sqrt(5.0)) / 2.0; // 1 / phi^2

    private static final double TOLERANCE = 1e-6;

    private static final double BALL_DIAMETER = BALL_RADIUS * 2.0;

    // Number of steps required to achieve desired tolerance
    private static final int NUM_ITERATIONS = (int) (Math
            .ceil(Math.log(TOLERANCE / (BALL_DIAMETER)) / Math.log(INV_PHI))) - 1;

    /**
     * Computes the Y position that the ball must be placed on in order not to clip
     * the curve.
     * 
     * @param f     the curve function to to compute the Y for.
     * @param ctx   the evaluation context containing variables other than x.
     * @param ballX the ball's x position.
     * @return the ball's minimum Y position.
     */
    public static double computeBallYOnCurve(Expr f, EvalContext ctx, double ballX) {
        var h = BALL_DIAMETER;
        var a = ballX - BALL_RADIUS;
        var b = ballX + BALL_RADIUS;

        var c = a + INV_PHI_E2 * h;
        var d = a + INV_PHI * h;

        var yc = evalAt(c, f, ctx, ballX);
        var yd = evalAt(d, f, ctx, ballX);

        for (var k = 0; k < NUM_ITERATIONS; k++) {
            if (yc < yd) {
                b = d;
                d = c;
                yd = yc;
                h = INV_PHI * h;
                c = a + INV_PHI_E2 * h;

                yc = evalAt(c, f, ctx, ballX);
            } else {
                a = c;
                c = d;
                yc = yd;
                h = INV_PHI * h;
                d = a + INV_PHI * h;

                yd = evalAt(d, f, ctx, ballX);
            }
        }

        // The algorithm obviously gives an interval rather than a single point. With a
        // low-enough
        // tolerance, the midpoint of the interval should be a very good approximation.
        ctx.varX = (yc < yd) ? (a + d) / 2.0 : (c + b) / 2.0;
        return evalAt(ctx.varX, f, ctx, ballX);
    }

    private static double evalAt(double x, Expr fn, EvalContext ctx, double ballX) {
        ctx.varX = x;
        return ballCurve(ballX - x) - fn.evaluate(ctx);
    }

    private static double ballCurve(double x) {
        return BALL_RADIUS - Math.sqrt(BALL_RADIUS * BALL_RADIUS - x * x);
    }
}
