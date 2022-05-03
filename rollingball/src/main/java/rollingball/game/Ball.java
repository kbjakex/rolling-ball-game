package rollingball.game;

import rollingball.game.FunctionStorage.Graph;

/**
 * The ball itself. Mostly an (x,y) pair.
 */
public final class Ball {
    /**
     * The radius of the ball in level coordinates.
     */
    public static final double RADIUS = 0.4;

    double x;
    double y;

    Graph collidingCurve;
    double lastCollisionTimestamp;

    Ball(Level level) {
        reset(level);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * Resets the ball to the start position of the level.
     * @param level the level
     */
    public void reset(Level level) {
        var start = level.getStart();
        this.x = start.x();
        this.y = start.y() + RADIUS;
        this.lastCollisionTimestamp = 0.0;
    }
}
