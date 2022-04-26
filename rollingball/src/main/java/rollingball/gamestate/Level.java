package rollingball.gamestate;

// TODO: Stub class intended to be filled in & integrated on week 5.
// This file exists for two reasons:
// 1. Using an integer for level id allows for the program to enter invalid state; an enum does not
// 2. The level definitions are likely going to be stored here as well, but we will see
public enum Level {
    LEVEL_1(
        "Level 1",
        -6.0, 0.0,
        6.0, 0.0
    );

    public final String name;
    public final double startX, startY;
    public final double endX, endY;

    private Level(String name, double startX, double startY, double endX, double endY) {
        this.name = name;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
}
