package rollingball.dao;

import java.util.List;

import rollingball.game.LevelBlueprint;

/**
 * Represents a data storage for the user progress.
 */
public interface UserProgressDao {

    /**
     * Represents a function consisting of a formula and a filtering condition, in string forms.
     * @param formula the formula of the function.
     * @param condition the condition of the function.
     */
    static final record Equation(String formula, String condition) {
    }

    /**
     * Information about the completion of a level.
     * @param levelBlueprint the level in question.
     * @param equations the equations used to solve the level.
     * @param scorePercentage the percentage of the score achieved, from 0 to 1 (1 is max).
     */
    static final record LevelCompletionInfo(LevelBlueprint level, List<Equation> equations, double scorePercentage) {
    }
    
    /**
     * Adds a new level completion. If the level had already been completed and the new score is higher,
     * the old completion will be replaced; if the score is lower, this is a no-op.
     */
    void addLevelCompletion(LevelCompletionInfo levelCompletionInfo);

    /**
     * Adds a new level completion. Convenience method for {@link #addLevelCompletion(LevelCompletionInfo)}.
     */
    default void addLevelCompletion(LevelBlueprint level, List<Equation> equationsUsed, double scorePercentage) {
        addLevelCompletion(new LevelCompletionInfo(level, equationsUsed, scorePercentage));
    }

    /**
     * Returns the level completion info for all the levels the user has completed so far.
     */
    List<LevelCompletionInfo> getLevelCompletions();

    /**
     * Returns the next known uncompleted level.
     */
    LevelBlueprint getNextUncompletedLevel();

    /**
     * Writes the user progress data to the disk.
     */
    void flushChanges() throws Exception;
}
