package rollingball.game;

import rollingball.game.GameSimulator.Ball;
import rollingball.game.Obstacles.Spike;

public sealed interface Obstacle permits Spike {

    boolean checkWouldKill(Ball ball);

}
