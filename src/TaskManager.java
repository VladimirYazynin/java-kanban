import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskCounter = 0;
    private final HashMap<TaskStatus, HashMap<Integer, Task>> tasks;

    public TaskManager() {
        tasks = new HashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            tasks.put(status, new HashMap<>());
        }
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();

        for (TaskStatus status : TaskStatus.values()) {
            HashMap<Integer, Task> currentMap = tasks.get(status);

            for (Task task : currentMap.values()) {
                allTasks.add(task);
            }
        }

        return allTasks;
    }

    public void deleteAllTasks() {
        for (TaskStatus status : TaskStatus.values()) {
            tasks.get(status).clear();
        }
    }

    public Task getTaskById(Integer id) {
        for (TaskStatus status : TaskStatus.values()) {
            HashMap<Integer, Task> currentMap = tasks.get(status);

            for (Task task : currentMap.values()) {
                if (task.getId() == id) {
                    return task;
                }
            }
        }

        return null;
    }

    public void createTask(Task task) {
        task.setId(taskCounter);

        if (task.getClass() == Epic.class)
            checkEpicStatus((Epic) task);

        HashMap<Integer, Task> currentMap = tasks.get(task.getStatus());
        currentMap.put(task.getId(), task);

        if (task.getClass() == Subtask.class) {
            Epic epic = (Epic) getTaskById(((Subtask) task).getIdEpictask());
            ArrayList<Integer> subtasksList = epic.getSubtasksId();
            subtasksList.add(task.getId());
            checkEpicStatus(epic);
        }

        taskCounter++;
    }

    public void updateTask(Task task) {
        TaskStatus oldStatus = getTaskById(task.getId()).getStatus();

        moveTask(task, oldStatus);

        if (task.getClass() == Epic.class)
            checkEpicStatus((Epic) task);
        if (task.getClass() == Subtask.class)
            checkEpicStatus((Epic) getTaskById(((Subtask) task).getIdEpictask()));
    }

    public void deleteById(Integer id) {
        if (getTaskById(id).getClass() == Epic.class) {
            Epic epic = (Epic) getTaskById(id);
            ArrayList<Integer> subtasksList = epic.getSubtasksId();
            for (TaskStatus status : TaskStatus.values()) {
                HashMap<Integer, Task> currentMap = tasks.get(status);

                for (Integer subtasksId : subtasksList) {
                    if (currentMap.containsKey(subtasksId)) {
                        currentMap.remove(subtasksId);
                    }
                }
            }
        }

        if (getTaskById(id).getClass() == Subtask.class) {
            Subtask subtask = (Subtask) getTaskById(id);
            Epic epic = (Epic)  getTaskById(subtask.getIdEpictask());
            epic.getSubtasksId().remove(subtask.getId());
        }

        for (TaskStatus status : TaskStatus.values()) {
            HashMap<Integer, Task> currentMap = tasks.get(status);

            if (currentMap.containsKey(id)) {
                currentMap.remove(id);
            }
        }

    }

    public ArrayList<Subtask> getAllSubasksOfEpicById(Integer id) {
        ArrayList<Subtask> resultList = new ArrayList<>();
        Epic epic = (Epic) getTaskById(id);
        ArrayList<Integer> subtasksIdList = epic.getSubtasksId();

        for (TaskStatus status : TaskStatus.values()) {
            HashMap<Integer, Task> currentMap = tasks.get(status);

            for (Task task : currentMap.values()) {
                for (Integer subtaskId : subtasksIdList) {
                    if (task.getId() == subtaskId)
                        resultList.add((Subtask) task);
                }
            }
        }

        return resultList;
    }

    private void moveTask(Task task, TaskStatus oldStatus) {
        if (task.getStatus() != oldStatus) {
            tasks.get(oldStatus).remove(task.getId());
            tasks.get(task.getStatus()).put(task.getId(), task);
        }
    }

    private void checkEpicStatus(Epic epic) {
        ArrayList<Integer> subtasksList = epic.getSubtasksId();
        TaskStatus oldStatus = epic.getStatus();

        if (subtasksList.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            int newCount = 0;
            int doneCount = 0;

            for (Integer subtaskId : subtasksList) {
                if (getTaskById(subtaskId).getStatus() == TaskStatus.NEW)
                    newCount++;
                if (getTaskById(subtaskId).getStatus() == TaskStatus.DONE)
                    doneCount++;
            }

            if (newCount == subtasksList.size() || doneCount == subtasksList.size()) {
                if (newCount == subtasksList.size()) epic.setStatus(TaskStatus.NEW);
                else epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }

        moveTask(epic, oldStatus);
    }
}
