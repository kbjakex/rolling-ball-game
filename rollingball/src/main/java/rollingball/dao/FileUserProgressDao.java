package rollingball.dao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rollingball.game.LevelBlueprint;

// TODO: Stub class intended to be integrated & tested for week 5. Changes might occur.
public final class FileUserProgressDao implements UserProgressDao {
    private static final int HEADER_MAGIC = 0x9C6D12E9;

    private final List<LevelCompletionInfo> levelCompletions;
    private final String saveFilePath;

    private LevelBlueprint nextUncompletedLevel;

    private FileUserProgressDao(String filePath, List<LevelCompletionInfo> levelCompletions, LevelBlueprint nextUncompleted) {
        this.levelCompletions = levelCompletions;
        this.saveFilePath = filePath;
        this.nextUncompletedLevel = nextUncompleted;
    }

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

    @Override
    public List<LevelCompletionInfo> getLevelCompletions() {
        return Collections.unmodifiableList(levelCompletions);
    }

    @Override
    public LevelBlueprint getNextUncompletedLevel() {
        return nextUncompletedLevel;
    }

    @Override
    public void flushChanges() throws Exception {
        try (var stream = new DataOutputStream(new FileOutputStream(saveFilePath))) {
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

    public static FileUserProgressDao empty(String filePath) {
        return new FileUserProgressDao(filePath, new ArrayList<>(), LevelBlueprint.LEVEL_1);
    }

    public static FileUserProgressDao loadFromFile(String filePath) throws IOException {
        try (var stream = new DataInputStream(new FileInputStream(filePath))) {
            if (stream.readInt() != HEADER_MAGIC) {
                throw new IOException("Invalid file header, not a valid user progress file");
            }

            var levelCompletions = readLevelCompletions(stream);

            LevelBlueprint nextUncompleted = null;
            try {
                nextUncompleted = LevelBlueprint.fromId(stream.readInt()).orElse(null);
            } catch (EOFException e) {
                // No next level
            }

            return new FileUserProgressDao(filePath, levelCompletions, nextUncompleted);
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
