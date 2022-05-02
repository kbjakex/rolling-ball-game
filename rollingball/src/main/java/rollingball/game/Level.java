package rollingball.game;

import java.util.Collections;
import java.util.List;

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

    private final LevelBlueprint levelBlueprint;
    private XY start;
    private XY end;

    public Level(LevelBlueprint level, XY start, XY end, List<Obstacle> obstacles) {
        this.levelBlueprint = level;
        this.start = start;
        this.end = end;
        this.obstacles = obstacles;
    }

    public String getName() {
        return levelBlueprint.getName();
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
        return levelBlueprint.next().createInstance();
    }

    public List<Obstacle> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }
}
