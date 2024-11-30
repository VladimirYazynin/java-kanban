package model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Subtask extends Task {

    private final Integer idEpictask;

    @SerializedName("subtaskType")
    private final TaskType taskType = TaskType.SUBTASK;

    public Integer getIdEpictask() {
        return idEpictask;
    }

    public Subtask(int id, String title, String description, TaskStatus status, Integer idEpictask) {
        super(id, title, description, status);
        this.idEpictask = idEpictask;
    }

    public Subtask(String title, String description, TaskStatus status, Integer idEpictask) {
        super(title, description, status);
        this.idEpictask = idEpictask;
    }

    public Subtask(int id, String title, String description, TaskStatus status, Integer idEpictask, LocalDateTime startTime, long duration) {
        super(id, title, description, status, startTime, duration);
        this.idEpictask = idEpictask;
    }

    public Subtask(String title, String description, TaskStatus status, Integer idEpictask, LocalDateTime startTime, long duration) {
        super(title, description, status, startTime, duration);
        this.idEpictask = idEpictask;
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public Integer getEpicId() {
        return idEpictask;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());

        sb.append(idEpictask);
        return sb.toString();
    }
}
