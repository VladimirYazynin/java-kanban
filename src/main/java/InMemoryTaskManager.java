import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private int taskCounter = 0;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();

        for (Task task : tasks.values()) {
            allTasks.add(task);
        }

        return allTasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();

        for (Epic epic : epics.values()) {
            allEpics.add(epic);
        }

        return allEpics;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            allSubtasks.add(subtask);
        }

        return allSubtasks;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    @Override
    public Task getTaskById(Integer id) {
        for (Task task : tasks.values()) {
            if (task.getId() == id) {
                historyManager.add(task);
                return task;
            }
        }

        return null;
    }

    @Override
    public Epic getEpicById(Integer id) {
        for (Epic epic : epics.values()) {
            if (epic.getId() == id) {
                historyManager.add(epic);
                return epic;
            }
        }

        return null;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getId() == id) {
                historyManager.add(subtask);
                return subtask;
            }
        }

        return null;
    }

    @Override
    public void createTask(Task task) {
        task.setId(taskCounter);
        tasks.put(task.getId(), task);
        taskCounter++;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(taskCounter);
        ArrayList<Integer> subtaskList = epic.getSubtasksId();

        if (subtaskList.contains(taskCounter))
            return;

        epics.put(epic.getId(), epic);
        checkEpicStatus(epic.getId());
        taskCounter++;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(taskCounter);

        if (subtask.getId() == subtask.getIdEpictask())
            return;

        ArrayList<Integer> subtasksList = getEpicById(subtask.getIdEpictask()).getSubtasksId();
        subtasks.put(subtask.getId(), subtask);
        subtasksList.add(subtask.getId());
        checkEpicStatus(subtask.getIdEpictask());
        taskCounter++;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId()))
            tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubtasksId().contains(epic.getId()))
                return;
            epics.put(epic.getId(), epic);
            checkEpicStatus(epic.getId());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            if (subtask.getId() == subtask.getIdEpictask())
                return;
            subtasks.put(subtask.getId(), subtask);
            checkEpicStatus(subtask.getIdEpictask());
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        if (tasks.containsKey(id))
            tasks.remove(id);
    }

    @Override
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

    @Override
    public void deleteSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            getEpicById(getSubtaskById(id).getIdEpictask()).getSubtasksId().remove(id); //Удаление id subtask из Epic
            subtasks.remove(id);
        }
    }

    @Override
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

    @Override
    public List<Task> getHistory() {
       return historyManager.getHistory();
    }

}

