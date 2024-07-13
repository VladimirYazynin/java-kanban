import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtasksId;

    public Epic(int id, String title, String description, TaskStatus status, List<Integer> subtasksId) {
        super(id, title, description, status);
        this.subtasksId = subtasksId;
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }
}
