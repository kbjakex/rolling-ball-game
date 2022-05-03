package rollingball;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import rollingball.functions.FunctionParser;
import rollingball.game.GameSimulator;
import rollingball.game.LevelBlueprint;

public class GameSimulatorTest {

    @Test
    public void testLevel1ModelSolutionWorks() {
        testLevel(LevelBlueprint.LEVEL_1, "1/3x+1.5", 9.0);
    }

    @Test
    public void testLevel2ModelSolutionWorks() {
        testLevel(LevelBlueprint.LEVEL_2, "-cos(x/1.2)*2-1.4", 12.6);
    }

    @Test
    public void testLevel3ModelSolutionWorks() {
        testLevel(LevelBlueprint.LEVEL_3, "-2.5*e^(-x^2/(2*1.5^2))+1+max(0,x/4)", 9.4);
    }

    @Test
    public void testLevel4ModelSolutionWorks() {
        testLevel(LevelBlueprint.LEVEL_4, "sin(t/2)+max(0,1.7*sin(x/2.15))", 8.25);
    }

    @Test
    public void testLevel5ModelSolutionWorks() {
        testLevel(LevelBlueprint.LEVEL_5, "4sin(t/2)+sin(t/4)+sin(t/6)", 7.9);
    }

    private void testLevel(LevelBlueprint blueprint, String expression, double targetTime) {
        var finished = new boolean[1];
        var simulationTime = new double[1];
        var simulation = new GameSimulator(blueprint.createInstance(), (won, time) -> {
            finished[0] = won;
            simulationTime[0] = time;
        });
        simulation.addGraph(FunctionParser.parse(expression, ""));

        simulation.togglePlaying();
        while (simulation.isPlaying()) {
            simulation.update();
        }

        assertEquals(true, finished[0]);
        assertEquals(true, simulationTime[0] < targetTime);
    }
    
}
