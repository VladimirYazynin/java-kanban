import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private Path historyFile;

    public FileBackedTaskManager(Path historyFile) {
        super();
        this.historyFile = historyFile;
    }

    public void save() {
        try (FileWriter fw = new FileWriter(String.valueOf(historyFile.getFileName()))) {
            
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", exception);
        }

    }
}
