package rollingball.dao;

import java.util.List;

// TODO: Stub class intended to be filled in on the next week.
public interface UserProgressDao {
    static final class LevelCompletionInfo {
        public final int levelNumber;
        public final List<String> equationsUsed;
        public final double scorePercentage;

        public LevelCompletionInfo(int levelNumber, List<String> equationsUsed, double scorePercentage) {
            this.levelNumber = levelNumber;
            this.equationsUsed = equationsUsed;
            this.scorePercentage = scorePercentage;
        }
    }
    
    void addLevelCompletion(LevelCompletionInfo levelCompletionInfo);

    default void addLevelCompletion(int levelId, List<String> equationsUsed, double scorePercentage) {
        addLevelCompletion(new LevelCompletionInfo(levelId, equationsUsed, scorePercentage));
    }

    List<LevelCompletionInfo> getLevelCompletions();

    int getNextUncompletedLevelId();

    void flushChanges() throws Exception;
}
