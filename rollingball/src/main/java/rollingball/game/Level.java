package rollingball.game;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class Level {
    /**
     * Test
     */
    public static final record XY(double x, double y) {
        /**
         * Creates an (x,y) coordinate pair from the parameters.
         * @param x the x coordinate
         * @param y the y coordinate
         * @return the coordinate pair
         */
        public static XY of(double x, double y) {
            return new XY(x, y);
        }
    }

    protected final List<Obstacle> obstacles;

    private final String name;
    private XY start;
    private XY end;

    public final Supplier<Level> nextLevel;

    public Level(String name, XY start, XY end, List<Obstacle> obstacles, Supplier<Level> nextLevel) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.obstacles = obstacles;

        this.nextLevel = nextLevel;
    }

    public String getName() {
        return name;
    }

    public XY getStart() {
        return start;
    }

    public XY getEnd() {
        return end;
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
