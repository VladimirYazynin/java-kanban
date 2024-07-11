public class Subtask extends Task {

    private final Integer idEpictask;

    public Integer getIdEpictask() {
        return idEpictask;
    }

    public Subtask(int id, String title, String description, TaskStatus status, Integer idEpictask) {
        super(id, title, description, status);
        this.idEpictask = idEpictask;
    }
}
