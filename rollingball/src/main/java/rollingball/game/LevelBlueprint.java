package rollingball.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import rollingball.game.Level.XY;
import rollingball.game.Obstacles.Spike;

/**
 * Contains the list of levels defined for the game. 
 * Each level has a name and id and contains the logic
 * for the placement of the obstacles in the level. 
 * A blueprint may also define the update logic for a level.
 */
public enum LevelBlueprint {
    LEVEL_1(0, "Level 1") {
        @Override
        public Level createInstance() {
            return new Level(
                    this,
                    XY.of(-6, 0), // Start (x,y)
                    XY.of(6, 4), // End (x,y)
                    Collections.emptyList() // Obstacles
            );
        }

        @Override
        public LevelBlueprint next() {
            return LevelBlueprint.LEVEL_2;
        }

        @Override
        public double computeScorePercentage(int numEquations, double timeSeconds) {
            // First level: should be a single straight line, which takes about 9 seconds to travel
            return 1.0 - (numEquations-1) / 3.0 - Math.max(0, timeSeconds - 9) / 3.0;
        }
    },
    LEVEL_2(1, "Level 2") {
        @Override
        public Level createInstance() {
            var obstacles = new ArrayList<Obstacle>();
            for (int i = 0; i < 8; i++) {
                obstacles.add(new Spike(0, i));
            }
            return new Level(this, XY.of(-6, 3), XY.of(6, 3), obstacles);
        }

        @Override
        public LevelBlueprint next() {
            return LevelBlueprint.LEVEL_3;
        }

        @Override
        public double computeScorePercentage(int numEquations, double timeSeconds) {
            // Target time 9.1 seconds, single equation
            return 1.0 - (numEquations-1) / 3.0 - Math.max(0, timeSeconds - 9.1) / 3.0;
        }
    },
    LEVEL_3(2, "Level 3") {
        @Override
        public Level createInstance() {
            var obstacles = new ArrayList<Obstacle>();
            for (int i = 0; i < 8; i++) {
                obstacles.add(new Spike(-4, -i));
                obstacles.add(new Spike(0, i));
                obstacles.add(new Spike(4, -i));
            }

            return new Level(this, XY.of(-7, -3), XY.of(7, -3), obstacles);
        }

        @Override
        public LevelBlueprint next() {
            return LevelBlueprint.LEVEL_4;
        }

        @Override
        public double computeScorePercentage(int numEquations, double timeSeconds) {
            // Target time 12.6 seconds, single equation
            return 1.0 - (numEquations-1) / 3.0 - Math.max(0, timeSeconds - 12.6) / 3.0;
        }
    },
    LEVEL_4(3, "Level 4") {
        static final class Level4 extends Level {
            private final List<Obstacle> basePositions;

            Level4(LevelBlueprint next, XY start, XY end, List<Obstacle> spikes) {
                super(next, start, end, spikes);
                this.basePositions = new ArrayList<>(spikes);
            }

            @Override
            public void onUpdate(double timeSeconds, double deltaTime) {
                var yOff = Math.sin(timeSeconds * 0.5) * 1.5;
                for (var i = 0; i < basePositions.size(); i++) {
                    var spike = (Spike) basePositions.get(i);
                    super.obstacles.set(i, new Spike(spike.x(), spike.y() + yOff));
                }
            }
        }

        @Override
        public Level createInstance() {
            var obstacles = new ArrayList<Obstacle>();
            for (int i = 0; i < 8; i++) {
                obstacles.add(new Spike(-i, 2));
                obstacles.add(new Spike(-i, -2));
                obstacles.add(new Spike(i + 1, 2 + 3 * Math.cos((i - 3) / 7.0 * Math.PI)));
                obstacles.add(new Spike(i + 1, -2 + 3 * Math.cos((i - 3) / 7.0 * Math.PI)));
            }

            return new Level4(this, XY.of(-6, 0), XY.of(6, 0), obstacles);
        }

        @Override
        public LevelBlueprint next() {
            return null;
        }

        @Override
        public double computeScorePercentage(int numEquations, double timeSeconds) {
            // Target time 8.3 seconds, two equations, OR, 9 seconds and 1 equation. Two-equation example solution:
            // -cos(x/1.2+.5)*0.8+0.8 | -0.5 < x < 8
            // 0
            return 1.0 - (numEquations-2) / 3.0 - Math.max(0, timeSeconds - 8.3) / 3.0;
        }
    };

    private final String name;
    private final int id;

    private LevelBlueprint(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns a human-readable name of the level, intended for display to the user.
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns an identifier that uniquely identifies the level, intended for saving to a file.
     * @return the id
     */
    public final int getId() {
        return id;
    }

    /**
     * Computes a score for the user based on the time and number of equations used.
     * @param numEquations the number of equations
     * @param timeSeconds the completion time in seconds
     * @return a score in range [0, 1], where 1 is perfect
     */
    public abstract double computeScorePercentage(int numEquations, double timeSeconds);

    /**
     * Creates a new instance of the level.
     * @return the instance
     */
    public abstract Level createInstance();

    /**
     * Returns the next level, or null if there is no next level.
     * @return the next level
     */
    public abstract LevelBlueprint next();

    /**
     * Returns the level with the given id, or an empty optional if there is no such level.
     * @param id the id
     * @return the level
     */
    public static Optional<LevelBlueprint> fromId(int id) {
        for (var level : values()) {
            if (level.getId() == id) {
                return Optional.of(level);
            }
        }
        return Optional.empty();
    }
}
