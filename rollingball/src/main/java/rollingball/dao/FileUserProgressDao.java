package rollingball.dao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: Stub class intended to be integrated & tested for week 5. Changes might occur.
public final class FileUserProgressDao implements UserProgressDao {
    private static final int HEADER_MAGIC = 0x9C6D12E9;

    private final List<LevelCompletionInfo> levelCompletions;
    private final String saveFilePath;

    private int nextUncompletedLevelId;

    private FileUserProgressDao(String filePath, List<LevelCompletionInfo> levelCompletions, int nextUncompleted) {
        this.levelCompletions = levelCompletions;
        this.saveFilePath = filePath;
        this.nextUncompletedLevelId = nextUncompleted;
    }

    @Override
    public void addLevelCompletion(LevelCompletionInfo levelCompletionInfo) {
        var it = levelCompletions.listIterator();
        while (it.hasNext()) {
            var levelCompletion = it.next();
            if (levelCompletion.levelNumber != levelCompletionInfo.levelNumber) {
                continue;
            }

            if (levelCompletion.scorePercentage < levelCompletionInfo.scorePercentage) {
                it.set(levelCompletionInfo); // replace if better than the previous solution
            }
            return;
        }

        // Otherwise, add
        levelCompletions.add(levelCompletionInfo);

        if (levelCompletionInfo.levelNumber == nextUncompletedLevelId) {
            // TODO Easy way to enter invalid state. See gamestate/Level.java.
            this.nextUncompletedLevelId += 1;
        }
    }

    @Override
    public List<LevelCompletionInfo> getLevelCompletions() {
        return Collections.unmodifiableList(levelCompletions);
    }

    @Override
    public int getNextUncompletedLevelId() {
        return nextUncompletedLevelId;
    }

    @Override
    public void flushChanges() throws Exception {
        try (var stream = new DataOutputStream(new FileOutputStream(saveFilePath))) {
            stream.writeInt(HEADER_MAGIC);
            stream.writeInt(levelCompletions.size());
            for (var levelCompletion : levelCompletions) {
                stream.writeInt(levelCompletion.levelNumber);
                stream.writeInt(levelCompletion.equationsUsed.size());
                for (var equation : levelCompletion.equationsUsed) {
                    stream.writeUTF(equation);
                }
                stream.writeDouble(levelCompletion.scorePercentage);
            }
            stream.writeInt(nextUncompletedLevelId);
        }
    }

    public static FileUserProgressDao loadFromFile(String filePath) throws IOException {
        try (var stream = new DataInputStream(new FileInputStream(filePath))) {
            if (stream.readInt() != HEADER_MAGIC) {
                throw new IOException("Invalid file header, not a valid user progress file");
            }

            var levelCompletions = readLevelCompletions(stream);
            var nextUncompleted = stream.readInt();

            return new FileUserProgressDao(filePath, levelCompletions, nextUncompleted);
        }
    }

    private static List<LevelCompletionInfo> readLevelCompletions(DataInputStream stream) throws IOException {
        var levelCompletions = new ArrayList<LevelCompletionInfo>();
        var numLevels = stream.readInt();
        for (int i = 0; i < numLevels; i++) {
            var levelNumber = stream.readInt();
            var numEquations = stream.readInt();
            var equationsUsed = new ArrayList<String>();
            for (int j = 0; j < numEquations; j++) {
                equationsUsed.add(stream.readUTF());
            }
            var scorePercentage = stream.readDouble();
            levelCompletions.add(new LevelCompletionInfo(levelNumber, equationsUsed, scorePercentage));
        }
        return levelCompletions;
    }

}
