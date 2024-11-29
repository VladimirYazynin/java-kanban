package model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtasksId;
    @SerializedName("type")
    private final TaskType taskType = TaskType.EPIC;
    private LocalDateTime endTime;

    public Epic(Integer id, String title, String description, TaskStatus status, List<Integer> subtasksId) {
        super(id, title, description, status);
        this.subtasksId = subtasksId;
        endTime = getEndTime();
    }

    public Epic(Integer id, String title, String description, TaskStatus status, List<Integer> subtasksId, LocalDateTime startTime, long duration) {
        super(id, title, description, status, startTime, duration);
        this.subtasksId = subtasksId;
        endTime = getEndTime();
    }

    public Epic(Integer id, String title, String description, TaskStatus status, LocalDateTime startTime, long duration) {
        super(id, title, description, status, startTime, duration);
        subtasksId = new ArrayList<>();
        endTime = getEndTime();
    }

    public Epic(Integer id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
        subtasksId = new ArrayList<>();
        endTime = getEndTime();
    }

    public Epic(String title, String description, TaskStatus status, List<Integer> subtasksId, LocalDateTime startTime, long duration) {
        super(title, description, status, startTime, duration);
        this.subtasksId = subtasksId;
        endTime = getEndTime();
    }

    public Epic(String title, String description, TaskStatus status, LocalDateTime startTime, long duration) {
        super(title, description, status, startTime, duration);
        subtasksId = new ArrayList<>();
        endTime = getEndTime();
    }


    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(",").append(endTime);
        for (Integer subtaskId : subtasksId)
            sb.append(",").append(subtaskId);
        return sb.toString();
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
