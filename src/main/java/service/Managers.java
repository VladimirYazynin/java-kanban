package service;

import java.nio.file.Paths;

public class Managers {

    private static final String saveFile = "taskInfo.csv";

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager() {
        return FileBackedTaskManager.loadFromFile(Paths.get(saveFile));
    }

}
