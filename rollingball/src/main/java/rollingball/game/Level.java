package rollingball.game;

import java.util.Collections;
import java.util.List;

/**
 * Represents the runtime state of a LevelBlueprint. Each LevelBlueprint defines, 
 * where and which obstacles to place in the level. LevelBlueprint, however, is immutable,
 * so an instance of Level is created (and for some levels, modified) instead.
 */
public class Level {
    /**
     * Represents a (x,y) coordinate pair.
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

    Level(LevelBlueprint level, XY start, XY end, List<Obstacle> obstacles) {
        this.levelBlueprint = level;
        this.start = start;
        this.end = end;
        this.obstacles = obstacles;
    }

    /**
     * Returns the {@link LevelBlueprint} that this level is based on. 
     * @return the blueprint
     */
    public LevelBlueprint getBlueprint() {
        return levelBlueprint;
    }

    /**
     * Returns the start coordinate of the level.
     * @return the (x,y) where the ball should spawn
     */
    public XY getStart() {
        return start;
    }

    /**
     * Returns the coordinates of the flag in this level.
     * @return the (x,y) coordinates of the end point of the level
     */
    public XY getEnd() {
        return end;
    }

    /**
     * Updates the state of the level. The method is overridden
     * for the levels that have special update logic and does nothing
     * by default.
     * @param timeSeconds the time in seconds since the simulaion started
     * @param deltaTime time, in seconds, since the last simulation update
     */
    public void onUpdate(double timeSeconds, double deltaTime) {
        for (var obstacle : obstacles) {
            obstacle.update(timeSeconds);
        }
    }

    /**
     * Returns the obstacles in the level.
     * @return an unmodifiable view of the obstacles
     */
    public List<Obstacle> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }
}
