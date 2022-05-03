package rollingball.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     */
    public static final class Spike implements Obstacle {
        /**
         * The radius of the spike in level coordinates.
         */
        public static final double RADIUS = 0.35;

        private double x;
        private double y;

        /**
         * Constructs a new spike.
         * @param x the x coordinate of the spike
         * @param y the y coordinate of the spike
         */
        public Spike(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public void setPosition(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        @Override
        public boolean checkWouldKill(Ball ball) {
            var dx = ball.getX() - x;
            var dy = ball.getY() - y;
            var spikeRadius = 0.35;
            var ballRadius = Ball.RADIUS;
            var collisionRadius = spikeRadius + ballRadius;
            return dx * dx + dy * dy < collisionRadius * collisionRadius;
        }
    }

    /**
     * A rotating wheel of spikes for maximum danger.
     */
    public static final class SpikeWheel implements Obstacle {
        private final List<Spike> spikes;
        private final double centerX;
        private final double centerY;
        private final int numEdges;
        private final int radius;
        private final double speed;

        /**
         * Constructs a new spike wheel.
         * @param x the x coordinate of the center of the wheel
         * @param y the y coordinate of the center of the wheel
         * @param numEdges the number of edges in the wheel
         * @param radius the radius of the wheel
         * @param speed the speed of rotation
         */
        public SpikeWheel(double x, double y, int numEdges, int radius, double speed) {
            this.spikes = new ArrayList<>(numEdges * (radius-1)+1);
            this.centerX = x;
            this.centerY = y;
            this.numEdges = numEdges;
            this.radius = radius;
            this.speed = speed;

            for (int i = 0; i < numEdges * (radius-1) + 1; ++i) {
                spikes.add(new Spike(0, 0));
            }
            update(0.0); // init spike positions
        }

        /**
         * Returns the spikes in the wheel.
         * @return an unmodifiable view of the spikes
         */
        public List<Spike> getSpikes() {
            return Collections.unmodifiableList(spikes);
        }

        @Override
        public void update(double timeSeconds) {
            var angle = timeSeconds * speed;
            var angleStep = 2.0 * Math.PI / numEdges;
            var index = 1; // skip center spike
            for (int i = 0; i < numEdges; i++) {
                var cos = Math.cos(angle);
                var sin = Math.sin(angle);
                for (int r = 1; r < radius; ++r) {
                    spikes.get(index++).setPosition(centerX + r * cos, centerY + r * sin);
                }

                angle += angleStep;
            }
        }
        
        @Override
        public boolean checkWouldKill(Ball ball) {
            for (var spike : spikes) {
                if (spike.checkWouldKill(ball)) {
                    return true;
                }
            }
            return false;
        }
    }
}
