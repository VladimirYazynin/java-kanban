import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtasksId;
    private final TaskType taskType = TaskType.EPIC;

    public Epic(int id, String title, String description, TaskStatus status, List<Integer> subtasksId) {
        super(id, title, description, status);
        this.subtasksId = subtasksId;
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        for (Integer subtaskId : subtasksId)
            sb.append(",").append(subtaskId);
        return sb.toString();
    }

}
