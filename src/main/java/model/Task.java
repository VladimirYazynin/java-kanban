package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {

    private Integer id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskType taskType = TaskType.TASK;
    private LocalDateTime startTime;
    private Duration duration;

    public Integer getId() {
        return id;
    }

    public void setId(Integer newId) {
        id = newId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus newStatus) {
        status = newStatus;
    }

    public Integer getEpicId() {
        return null;
    }

    public Task(Integer id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ofMinutes(15);
    }

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ofMinutes(15);
    }

    public Task(Integer id, String title, String description, TaskStatus status, LocalDateTime startTime, long duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        if (startTime == null)
            this.startTime = LocalDateTime.now();
        else
            this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    }

    public Task(String title, String description, TaskStatus status, LocalDateTime startTime, long duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        if (startTime == null)
            this.startTime = LocalDateTime.now();
        else
            this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s", id, getTaskType(), title, status, description, startTime, duration, startTime);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime newStartTime) {
        startTime = newStartTime;
    }

    public long getDuration() {
        return duration.toMinutes();
    }

    public void setDuration(long newDuration) {
        duration = Duration.ofMinutes(newDuration);
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public int compareTo(Task o) {
        return this.startTime.compareTo(o.getStartTime());
    }
}
