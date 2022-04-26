package rollingball.gamestate;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class Level {
    public static final record XY(double x, double y) {
        public static XY of(double x, double y) {
            return new XY(x, y);
        }
    }

    private final List<Obstacle> obstacles;
    
    public final String name;
    public XY start;
    public XY end;

    public final Supplier<Level> nextLevel;

    public Level(String name, XY start, XY end, List<Obstacle> obstacles, Supplier<Level> nextLevel) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.obstacles = obstacles;

        this.nextLevel = nextLevel;
    }

    public void onUpdate(double timeSeconds, double deltaTime) {
    }

    public Level nextLevel() {
        return nextLevel.get();
    }

    public List<Obstacle> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }
}
