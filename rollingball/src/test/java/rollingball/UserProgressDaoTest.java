package rollingball;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import rollingball.dao.FileUserProgressDao;
import rollingball.dao.UserProgressDao;
import rollingball.dao.UserProgressDao.LevelCompletionInfo;
import rollingball.game.LevelBlueprint;

public class UserProgressDaoTest {
    
    @TempDir
    File directory;

    File tempFile;

    @BeforeEach
    public void init(@TempDir File dir) throws IOException {
        tempFile = File.createTempFile("userdata", null, dir);
    }

    @Test
    public void testLoadingCorruptedSaveDoesNotThrow() {
        assertDoesNotThrow(() -> FileUserProgressDao.loadFromFile(tempFile));
    }

    @Test
    public void testLoadingNonExistingFileThrowsFileNotFound() {
        assertThrowsExactly(FileNotFoundException.class, () -> FileUserProgressDao.loadFromFile(new File("/non/existing/file")));
    }

    @Test
    public void testEmptyDaoHasCorrectDefaults() {
        var dao = FileUserProgressDao.empty(tempFile);
        assertEquals(LevelBlueprint.LEVEL_1, dao.getNextUncompletedLevel());
        assertEquals(0, dao.getLevelCompletions().size());
    }

    @Test
    public void testSavingAndLoadingRetainsData() {
        LevelCompletionInfo completion = new LevelCompletionInfo(
            LevelBlueprint.LEVEL_1, 
            Arrays.asList(new UserProgressDao.Equation("3x+5", "5 < x<3")), 
            0.75
        );

        UserProgressDao reg = FileUserProgressDao.empty(tempFile);
        reg.addLevelCompletion(completion);
        assertDoesNotThrow(() -> reg.flushChanges());

        UserProgressDao[] loaded = new UserProgressDao[1]; 
        
        assertDoesNotThrow(() -> loaded[0] = FileUserProgressDao.loadFromFile(tempFile));
        assertEquals(1, loaded[0].getLevelCompletions().size());
        assertEquals(LevelBlueprint.LEVEL_2, loaded[0].getNextUncompletedLevel());
        LevelCompletionInfo loadedCompletion = loaded[0].getLevelCompletions().get(0);
        assertEquals(completion.level(), loadedCompletion.level());
        assertEquals(completion.equations(), loadedCompletion.equations());
        assertEquals(completion.scorePercentage(), loadedCompletion.scorePercentage());
    }

    @Test
    public void testBetterSolutionReplacesOldSolution() {
        UserProgressDao dao = FileUserProgressDao.empty(tempFile);
        dao.addLevelCompletion(
            LevelBlueprint.LEVEL_1, 
            Arrays.asList(new UserProgressDao.Equation("3x+4", "6 < x<3")), 
            0.74
        );
        dao.addLevelCompletion(
            LevelBlueprint.LEVEL_2, 
            Arrays.asList(new UserProgressDao.Equation("3x+5", "5 < x<3")), 
            0.75
        );
        dao.addLevelCompletion(
            LevelBlueprint.LEVEL_2, 
            Arrays.asList(new UserProgressDao.Equation("3x+6", "4 < x<3")), 
            0.76
        );
        assertEquals(2, dao.getLevelCompletions().size());
        assertEquals(LevelBlueprint.LEVEL_3, dao.getNextUncompletedLevel());

        LevelCompletionInfo loadedCompletion = dao.getLevelCompletions().get(1);
        assertEquals(LevelBlueprint.LEVEL_2, loadedCompletion.level());
        assertEquals(Arrays.asList(new UserProgressDao.Equation("3x+6", "4 < x<3")), loadedCompletion.equations());
        assertEquals(0.76, loadedCompletion.scorePercentage());

        // Make sure that level 1's solution was not changed
        loadedCompletion = dao.getLevelCompletions().get(0);
        assertEquals(LevelBlueprint.LEVEL_1, loadedCompletion.level());
        assertEquals(Arrays.asList(new UserProgressDao.Equation("3x+4", "6 < x<3")), loadedCompletion.equations());
        assertEquals(0.74, loadedCompletion.scorePercentage());
    }
}
