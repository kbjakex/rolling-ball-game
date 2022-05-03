package rollingball.game;

import rollingball.game.GameSimulator.Ball;

/**
 * List of obstacles currently in the game.
 * The classes are defined here instead of in the interface itself,
 * because the interface is sealed, and the permitted classes
 * must be defined before declaring a sealed class.
 */
public final class Obstacles {
    private Obstacles() { // make non-instantiable
    }

    /**
     * A spike obstacle.
     * @param x the x coordinate of the spike
     * @param y the y coordinate of the spike
     */
    public static final record Spike(double x, double y) implements Obstacle {
        @Override
        public boolean checkWouldKill(Ball ball) {
            var dx = ball.getX() - x;
            var dy = ball.getY() - y;
            return dx * dx + dy * dy < 0.5 * 0.5;
        }
    }
}
