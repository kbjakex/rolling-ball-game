package rollingball.game;

import rollingball.game.GameSimulator.Ball;

public final class Obstacles {
    private Obstacles() {
    }

    public static final record Spike(double x, double y) implements Obstacle {
        @Override
        public boolean checkWouldKill(Ball ball) {
            var dx = ball.getX() - x;
            var dy = ball.getY() - y;
            return dx * dx + dy * dy < 0.5 * 0.5;
        }
    }
}
