package rollingball.game;

import rollingball.game.Obstacles.Spike;
import rollingball.game.Obstacles.SpikeWheel;

/**
 * An interface implemented by all obstacles.
 * For the simulation, it is enough to know if an obstacle
 * would kill the ball at any given point in time.
 */
public sealed interface Obstacle permits Spike, SpikeWheel {

    /**
     * Checks if the ball is at a position where it would
     * be killed by the obstacle in its current state.
     * @param ball the ball
     * @return true if the ball should die, false otherwise
     */
    boolean checkWouldKill(Ball ball);

    default void update(double timeSeconds) {
    }

}
