package rollingball.dao;

import java.util.List;

import rollingball.game.LevelBlueprint;

public interface UserProgressDao {

    static final record Equation(String formula, String condition) {}
    static final record LevelCompletionInfo(LevelBlueprint level, List<Equation> equations, double scorePercentage) {
    }
    
    void addLevelCompletion(LevelCompletionInfo levelCompletionInfo);

    default void addLevelCompletion(LevelBlueprint level, List<Equation> equationsUsed, double scorePercentage) {
        addLevelCompletion(new LevelCompletionInfo(level, equationsUsed, scorePercentage));
    }

    List<LevelCompletionInfo> getLevelCompletions();

    LevelBlueprint getNextUncompletedLevel();

    void flushChanges() throws Exception;
}
