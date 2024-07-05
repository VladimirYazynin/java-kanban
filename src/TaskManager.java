import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskCounter = 0;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();

        for (Task task : tasks.values()) {
            allTasks.add(task);
        }

        return allTasks;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();

        for (Epic epic : epics.values()) {
            allEpics.add(epic);
        }

        return allEpics;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            allSubtasks.add(subtask);
        }

        return allSubtasks;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    public Task getTaskById(Integer id) {
        for (Task task : tasks.values()) {
            if (task.getId() == id) {
                return task;
            }
        }

        return null;
    }

    public Epic getEpicById(Integer id) {
        for (Epic epic : epics.values()) {
            if (epic.getId() == id) {
                return epic;
            }
        }

        return null;
    }

    public Subtask getSubtaskById(Integer id) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getId() == id) {
                return subtask;
            }
        }

        return null;
    }

    public void createTask(Task task) {
        task.setId(taskCounter);
        tasks.put(task.getId(), task);
        taskCounter++;
    }

    public void createEpic(Epic epic) {
        epic.setId(taskCounter);
        epics.put(epic.getId(), epic);
        checkEpicStatus(epic.getId());
        taskCounter++;
    }

    public void createSubtask(Subtask subtask) {
        ArrayList<Integer> subtasksList = getEpicById(subtask.getIdEpictask()).getSubtasksId();

        subtask.setId(taskCounter);
        subtasks.put(subtask.getId(), subtask);
        subtasksList.add(subtask.getId());
        checkEpicStatus(subtask.getIdEpictask());
        taskCounter++;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        checkEpicStatus(epic.getId());
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        checkEpicStatus(subtask.getIdEpictask());
    }

    public void deleteTaskById(Integer id) {
        if (tasks.containsKey(id))
            tasks.remove(id);
    }

    public void deleteEpicById(Integer id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subtasksList = epics.get(id).getSubtasksId();

            for (Integer subtaskId : subtasksList) {
                if (subtasks.containsKey(subtaskId))
                    subtasks.remove(subtaskId);
            }

            epics.remove(id);
        }
    }

    public void deleteSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            getEpicById(getSubtaskById(id).getIdEpictask()).getSubtasksId().remove(id); //Удаление id subtask из Epic
            subtasks.remove(id);
        }
    }

    public ArrayList<Subtask> getAllSubtasksOfEpicById(Integer id) {
        ArrayList<Subtask> resultList = new ArrayList<>();

        for (Integer subtaskId : getEpicById(id).getSubtasksId()) {
            resultList.add(subtasks.get(subtaskId));
        }

        return resultList;
    }

    private void checkEpicStatus(Integer epicId) {
        Epic epic = getEpicById(epicId);
        ArrayList<Integer> subtasksList = epic.getSubtasksId();

        if (subtasksList.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            int newCount = 0;
            int doneCount = 0;

            for (Integer subtaskId : subtasksList) {
                if (getSubtaskById(subtaskId).getStatus() == TaskStatus.NEW)
                    newCount++;
                if (getSubtaskById(subtaskId).getStatus() == TaskStatus.DONE)
                    doneCount++;
            }

            if (newCount == subtasksList.size() || doneCount == subtasksList.size()) {
                if (newCount == subtasksList.size()) epic.setStatus(TaskStatus.NEW);
                else epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }
}
