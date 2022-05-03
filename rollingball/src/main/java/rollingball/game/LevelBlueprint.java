package rollingball.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            // Takes about 9 seconds, single equation
            // Example solution: 1/3x+1.5
            return score(numEquations, timeSeconds, 1, 9.0);
        }
    },
    LEVEL_2(1, "Level 2") {
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
            return LevelBlueprint.LEVEL_3;
        }

        @Override
        public double computeScorePercentage(int numEquations, double timeSeconds) {
            // Target time 12.6 seconds, single equation
            // Example solution: -cos(x/1.2)*2-1.4
            return score(numEquations, timeSeconds, 1, 12.6);
        }
    },
    LEVEL_3(2, "Level 3") {
        @Override
        public Level createInstance() {
            var obstacles = new ArrayList<Obstacle>();
            for (int i = 0; i < 8; i++) {
                obstacles.add(new Spike(0, i));
            }
            for (int i = 0; i < 5; ++i) {
                obstacles.add(new Spike(-3-i, 0));
                obstacles.add(new Spike(3+i, 0));
            }
            return new Level(this, XY.of(-6, 3), XY.of(6, 3), obstacles);
        }

        @Override
        public LevelBlueprint next() {
            return LevelBlueprint.LEVEL_4;
        }

        @Override
        public double computeScorePercentage(int numEquations, double timeSeconds) {
            // Target time 9.4 seconds, single equation
            // Example solution: -2.5*e^(-x^2 / (2*1.5^2))+1+max(0,x/4)
            return score(numEquations, timeSeconds, 1, 9.4);
        }
    },
    LEVEL_4(3, "Level 4") {
        static final class Level4 extends Level {
            private final List<XY> basePositions;

            Level4(LevelBlueprint level, XY start, XY end, List<Obstacle> spikes) {
                super(level, start, end, spikes);
                this.basePositions = spikes.stream().map(s -> (Spike)s).map(s -> XY.of(s.getX(), s.getY())).collect(Collectors.toList());
            }

            @Override
            public void onUpdate(double timeSeconds, double deltaTime) {
                var yOff = Math.sin(timeSeconds * 0.5) * 1.5;
                for (var i = 0; i < obstacles.size(); i++) {
                    var spike = (Spike) obstacles.get(i);
                    var basePos = basePositions.get(i);

                    spike.setPosition(basePos.x(), basePos.y() + yOff);
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
            return LevelBlueprint.LEVEL_5;
        }

        @Override
        public double computeScorePercentage(int numEquations, double timeSeconds) {
            // Target time 8.25 seconds, single equation
            // Example solution: sin(t/2)+max(0, 1.7*sin(x/2.15))
            return score(numEquations, timeSeconds, 2, 8.3);
        }
    },
    LEVEL_5(4, "Level 5") {
        @Override
        public Level createInstance() {
            var wheel = new Obstacles.SpikeWheel(0.0, 0.0, 5, 5, -0.8);

            return new Level(this, XY.of(-6, 0), XY.of(6, 0), Arrays.asList(wheel));
        }

        @Override
        public LevelBlueprint next() {
            return null; // last level
        }

        @Override
        public double computeScorePercentage(int numEquations, double timeSeconds) {
            // Target time 7.9 seconds, one equation. Example solution:
            // 4sin(t/2)+sin(t/4)+sin(t/6)
            return score(numEquations, timeSeconds, 1, 7.9);
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

    private static double score(int numEquations, double timeSeconds, int targetEquations, double targetTime) {
        return 1.0 - (numEquations-targetEquations) / 3.0 - Math.max(0, timeSeconds - targetTime) / 3.0;
    }
}
