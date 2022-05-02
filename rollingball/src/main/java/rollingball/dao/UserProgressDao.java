package rollingball.dao;

import java.util.List;

import rollingball.game.LevelBlueprint;

public interface UserProgressDao {
    static final class LevelCompletionInfo {
        public final LevelBlueprint level;
        public final List<String> equationsUsed;
        public final double scorePercentage;

        public LevelCompletionInfo(LevelBlueprint level, List<String> equationsUsed, double scorePercentage) {
            this.level = level;
            this.equationsUsed = equationsUsed;
            this.scorePercentage = scorePercentage;
        }
    }
    
    void addLevelCompletion(LevelCompletionInfo levelCompletionInfo);

    default void addLevelCompletion(LevelBlueprint level, List<String> equationsUsed, double scorePercentage) {
        addLevelCompletion(new LevelCompletionInfo(level, equationsUsed, scorePercentage));
    }

    List<LevelCompletionInfo> getLevelCompletions();

    LevelBlueprint getNextUncompletedLevel();

    void flushChanges() throws Exception;
}
