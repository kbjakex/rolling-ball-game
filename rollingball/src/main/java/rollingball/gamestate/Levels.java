package rollingball.gamestate;

import java.util.ArrayList;
import java.util.Collections;

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
            return new Level(name, XY.of(-6, 3), XY.of(6, 3), obstacles, () -> null);
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
