package rollingball.gamestate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rollingball.gamestate.Level.XY;
import rollingball.gamestate.Obstacles.Spike;

public enum Levels {
    LEVEL_1("Level 1") {
        @Override
        public Level createInstance() {
            return new Level(
                    name,
                    XY.of(-6, 0), // Start (x,y)
                    XY.of(6, 4), // End (x,y)
                    Collections.emptyList(), // Obstacles
                    () -> LEVEL_2.createInstance() // Next level
            );
        }
    },
    LEVEL_2("Level 2") {
        @Override
        public Level createInstance() {
            var obstacles = new ArrayList<Obstacle>();
            for (int i = 0; i < 8; i++) {
                obstacles.add(new Spike(0, i));
            }
            return new Level(name, XY.of(-6, 3), XY.of(6, 3), obstacles, () -> LEVEL_3.createInstance());
        }
    },
    LEVEL_3("Level 3") {
        @Override
        public Level createInstance() {
            var obstacles = new ArrayList<Obstacle>();
            for (int i = 0; i < 8; i++) {
                obstacles.add(new Spike(-4, -i));
                obstacles.add(new Spike(0, i));
                obstacles.add(new Spike(4, -i));
            }

            return new Level(name, XY.of(-7, -3), XY.of(7, -3), obstacles, () -> LEVEL_4.createInstance());
        }
    },
    LEVEL_4("Level 4") {
        static final class Level4 extends Level {
            private final List<Obstacle> basePositions;

            Level4(String name, XY start, XY end, List<Obstacle> spikes) {
                super(name, start, end, spikes, () -> null);
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

            return new Level4(name, XY.of(-6, 0), XY.of(6, 0), obstacles);
        }
    };

    public final String name;

    private Levels(String name) {
        this.name = name;
    }

    public abstract Level createInstance();

    void onUpdate(Level levelInstance, double timeSeconds) {
    }
}
