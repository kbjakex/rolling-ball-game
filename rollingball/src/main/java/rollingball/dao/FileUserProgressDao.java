package rollingball.dao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rollingball.game.LevelBlueprint;

/**
 * A file-backed implementation of {@link UserProgressDao}.
 */
public final class FileUserProgressDao implements UserProgressDao {
    private static final int HEADER_MAGIC = 0x9C6D12E9;

    private final List<LevelCompletionInfo> levelCompletions;
    private final File saveFile;

    private LevelBlueprint nextUncompletedLevel;

    private FileUserProgressDao(File file, List<LevelCompletionInfo> levelCompletions, LevelBlueprint nextUncompleted) {
        this.levelCompletions = levelCompletions;
        this.saveFile = file;
        this.nextUncompletedLevel = nextUncompleted;
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public void addLevelCompletion(LevelCompletionInfo levelCompletionInfo) {
        var it = levelCompletions.listIterator();
        while (it.hasNext()) {
            var levelCompletion = it.next();
            if (levelCompletion.level() != levelCompletionInfo.level()) {
                continue;
            }

            if (levelCompletion.scorePercentage() < levelCompletionInfo.scorePercentage()) {
                it.set(levelCompletionInfo); // replace if better than the previous solution
            }
            return;
        }

        // Otherwise, add
        levelCompletions.add(levelCompletionInfo);

        if (levelCompletionInfo.level() == nextUncompletedLevel) {
            this.nextUncompletedLevel = this.nextUncompletedLevel.next();
        }
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public List<LevelCompletionInfo> getLevelCompletions() {
        return Collections.unmodifiableList(levelCompletions);
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public LevelBlueprint getNextUncompletedLevel() {
        return nextUncompletedLevel;
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public void flushChanges() throws Exception {
        try (var stream = new DataOutputStream(new FileOutputStream(saveFile))) {
            stream.writeInt(HEADER_MAGIC);
            stream.writeInt(levelCompletions.size());
            for (var levelCompletion : levelCompletions) {
                stream.writeInt(levelCompletion.level().getId());
                stream.writeInt(levelCompletion.equations().size());
                for (var equation : levelCompletion.equations()) {
                    stream.writeUTF(equation.formula());
                    stream.writeUTF(equation.condition());
                }
                stream.writeDouble(levelCompletion.scorePercentage());
            }

            if (nextUncompletedLevel != null) {
                stream.writeInt(nextUncompletedLevel.getId());
            }
        }
    }

    /**
     * Creates an empty user progress data storage. This will not modify the file
     * until {@link #flushChanges()} is called, but will then override any contents
     * there might be in the file. The file contents will not be read at any point.
     * <p>
     * The storage will be initialized with no completed levels, and the next level
     * to be completed will be the first level in the game.
     * 
     * @param file the file to save the data to.
     * @return an instance of this class
     */
    public static FileUserProgressDao empty(File file) {
        return new FileUserProgressDao(file, new ArrayList<>(), LevelBlueprint.LEVEL_1);
    }

    /**
     * Loads the user progress data from the given file.
     * @param file the file to load the data from.
     * @return an instance of this class, or null if the file is corrupted.
     * @throws IOException
     */
    public static FileUserProgressDao loadFromFile(File file) throws IOException {
        try (var stream = new DataInputStream(new FileInputStream(file))) {
            if (stream.readInt() != HEADER_MAGIC) {
                throw new IOException("Invalid file header, not a valid user progress file");
            }

            var levelCompletions = readLevelCompletions(stream);

            LevelBlueprint nextUncompleted = null;
            try {
                nextUncompleted = LevelBlueprint.fromId(stream.readInt()).orElse(null);
            } catch (EOFException e) {
                // No next level - that's fine
            }

            return new FileUserProgressDao(file, levelCompletions, nextUncompleted);
        } catch (EOFException ex) {
            return null;
        }
    }

    private static List<LevelCompletionInfo> readLevelCompletions(DataInputStream stream) throws IOException {
        var levelCompletions = new ArrayList<LevelCompletionInfo>();
        var numLevels = stream.readInt();
        for (int i = 0; i < numLevels; i++) {
            var level = LevelBlueprint.fromId(stream.readInt()).get();
            var numEquations = stream.readInt();
            var equationsUsed = new ArrayList<Equation>();
            for (int j = 0; j < numEquations; j++) {
                equationsUsed.add(new Equation(stream.readUTF(), stream.readUTF()));
            }
            var scorePercentage = stream.readDouble();
            levelCompletions.add(new LevelCompletionInfo(level, equationsUsed, scorePercentage));
        }
        return levelCompletions;
    }

}
