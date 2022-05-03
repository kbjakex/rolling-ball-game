package rollingball.game;

import rollingball.game.Obstacles.Spike;
import rollingball.game.Obstacles.SpikeWheel;

/**
 * An interface implemented by all obstacles.
 */
public sealed interface Obstacle permits Spike, SpikeWheel {

    /**
     * Checks if the ball is at a position where it would
     * be killed by the obstacle in its current state.
     * @param ball the ball
     * @return true if the ball should die, false otherwise
     */
    boolean checkWouldKill(Ball ball);

    /**
     * Updates the obstacle's state. This method is called once per frame.
     * @param timeSeconds time in seconds since the simulation started
     */
    default void update(double timeSeconds) {
    }

}
