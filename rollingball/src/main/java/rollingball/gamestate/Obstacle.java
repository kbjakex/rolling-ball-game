package rollingball.gamestate;

import rollingball.gamestate.GameState.Ball;
import rollingball.gamestate.Obstacles.Spike;

public sealed interface Obstacle permits Spike {

    boolean checkWouldKill(Ball ball);
    
}
