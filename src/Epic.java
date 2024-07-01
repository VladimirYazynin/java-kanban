import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtasksId;

    public Epic(int id, String title, String description, TaskStatus status, ArrayList<Integer> subtasksId) {
        super(id, title, description, status);
        this.subtasksId = subtasksId;
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }
}
