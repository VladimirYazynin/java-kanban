package service;

import exception.NotFoundException;
import exception.ValidationException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {

    private int taskCounter = 0;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private boolean checkTaskTimeValidity(Task taskForAdd, Task taskFromList) {
        if (taskForAdd.getId() == taskFromList.getId())
            return true;
        return taskForAdd.getStartTime().isAfter(taskFromList.getEndTime()) ||
                taskForAdd.getEndTime().isBefore(taskFromList.getStartTime());
    }

    protected void validateTask(Task task) {
        boolean isValid = getPrioritizedTasks().stream()
                .noneMatch(pt -> checkTaskTimeValidity(task, pt));
        if (!isValid)
            throw new ValidationException("Произошло пересечение с другой задачей");
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();

        for (Task task : tasks.values()) {
            allTasks.add(task);
        }

        return allTasks;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> allEpics = new ArrayList<>();

        for (Epic epic : epics.values()) {
            allEpics.add(epic);
        }

        return allEpics;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> allSubtasks = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            allSubtasks.add(subtask);
        }

        return allSubtasks;
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(findTaskById(id));
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Integer id : epics.keySet()) {
            for (Integer subtaskId : getEpicById(id).getSubtasksId()) {
                historyManager.remove(subtaskId);
                prioritizedTasks.remove(findSubtaskById(subtaskId));
            }
            historyManager.remove(id);
        }
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(findSubtaskById(id));
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = tasks.get(id);
        if (task != null)
            historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null)
            historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null)
            historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        task.setId(taskCounter);
        validateTask(task);
        prioritizedTasks.add(task);
        tasks.put(task.getId(), task);
        taskCounter++;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(taskCounter);
        List<Integer> subtaskList = epic.getSubtasksId();

        if (subtaskList.contains(taskCounter))
            return;

        epics.put(epic.getId(), epic);
        calculateEpicState(epic.getId());
        taskCounter++;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(taskCounter);

        if (subtask.getId() == subtask.getIdEpictask())
            return;

        validateTask(subtask);
        prioritizedTasks.add(subtask);

        List<Integer> subtasksList = findEpicById(subtask.getIdEpictask()).getSubtasksId();
        subtasks.put(subtask.getId(), subtask);
        subtasksList.add(subtask.getId());
        calculateEpicState(subtask.getIdEpictask());
        taskCounter++;
    }

    @Override
    public void updateTask(Task task) {
        Task original = findTaskById(task.getId());
        if (original == null)
            throw new NotFoundException("Задача с id " + task.getId() + " не найдена");

        validateTask(task);
        prioritizedTasks.remove(original);
        prioritizedTasks.add(task);

        if (tasks.containsKey(task.getId()))
            tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubtasksId().contains(epic.getId()))
                return;
            epics.put(epic.getId(), epic);
            calculateEpicState(epic.getId());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask.getId() == subtask.getIdEpictask())
            return;

        Subtask original = findSubtaskById(subtask.getId());
        if (original == null)
            throw new NotFoundException("Подзадача с id " + subtask.getId() + " не найдена");

        validateTask(subtask);
        prioritizedTasks.remove(original);
        prioritizedTasks.add(subtask);

        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            calculateEpicState(subtask.getIdEpictask());
        }
    }

    @Override
    public void updateTaskById(Integer id, Task task) {
        Task original = findTaskById(id);
        if (original == null)
            throw new NotFoundException("Задача с id " + task.getId() + " не найдена");

        validateTask(task);
        prioritizedTasks.remove(original);
        prioritizedTasks.add(task);

        if (tasks.containsKey(id))
            tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpicId(Integer id, Epic epic) {
        if (epics.containsKey(id)) {
            if (epic.getSubtasksId().contains(id))
                return;
            epics.put(id, epic);
            calculateEpicState(id);
        }
    }

    @Override
    public void updateSubtaskById(Integer id, Subtask subtask) {
        if (id == subtask.getIdEpictask())
            return;

        Subtask original = findSubtaskById(subtask.getId());
        if (original == null)
            throw new NotFoundException("Подзадача с id " + subtask.getId() + " не найдена");

        validateTask(subtask);
        prioritizedTasks.remove(original);
        prioritizedTasks.add(subtask);

        if (subtasks.containsKey(id)) {
            subtasks.put(id, subtask);
            calculateEpicState(subtask.getIdEpictask());
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            prioritizedTasks.remove(findTaskById(id));
            tasks.remove(id);
        }
    }

    @Override
    public void deleteEpicById(Integer id) {
        if (epics.containsKey(id)) {
            List<Integer> subtasksList = epics.get(id).getSubtasksId();

            for (Integer subtaskId : subtasksList) {
                if (subtasks.containsKey(subtaskId)) {
                    prioritizedTasks.remove(findSubtaskById(subtaskId));
                    subtasks.remove(subtaskId);
                    historyManager.remove(subtaskId);
                }
            }

            historyManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            getEpicById(getSubtaskById(id).getIdEpictask()).getSubtasksId().remove(id); //Удаление id subtask из model.Epic
            historyManager.remove(id);
            prioritizedTasks.remove(findSubtaskById(id));
            subtasks.remove(id);
        }
    }

    @Override
    public List<Subtask> getAllSubtasksOfEpicById(Integer id) {
        List<Subtask> resultList = new ArrayList<>();

        for (Integer subtaskId : getEpicById(id).getSubtasksId()) {
            resultList.add(subtasks.get(subtaskId));
        }

        return resultList;
    }

    private void calculateEpicState(Integer epicId) {
        Epic epic = findEpicById(epicId);
        if (epic == null)
            throw new NotFoundException("Эпик с id " + epicId + " не найден");

        List<Integer> subtasksList = epic.getSubtasksId();
        int duration = 0;

        if (subtasksList.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            epic.setDuration(duration);
        } else {
            int newCount = 0;
            int doneCount = 0;
            LocalDateTime epicStartTime = LocalDateTime.MAX;

            for (Integer subtaskId : subtasksList) {
                Subtask subtask = findSubtaskById(subtaskId);
                if (subtask == null)
                    throw new NotFoundException("Подзадача с id " + subtaskId + " не найдена");

                duration += subtask.getDuration();
                if (subtask.getStartTime().isBefore(epicStartTime))
                    epicStartTime = subtask.getStartTime();

                if (subtask.getStatus() == TaskStatus.NEW)
                    newCount++;
                if (subtask.getStatus() == TaskStatus.DONE)
                    doneCount++;
            }

            epic.setDuration(duration);
            epic.setStartTime(epicStartTime);
            epic.setEndTime(epicStartTime.plus(Duration.ofMinutes(duration)));

            if (newCount == subtasksList.size() || doneCount == subtasksList.size()) {
                if (newCount == subtasksList.size()) epic.setStatus(TaskStatus.NEW);
                else epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    @Override
    public List<Task> getAll() {
        List<Task> result = new ArrayList<>();

        result.addAll(getAllTasks());
        result.addAll(getAllEpics());
        result.addAll(getAllSubtasks());

        return result;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private Task findTaskById(Integer id) {
        return tasks.get(id);
    }

    private Epic findEpicById(Integer id) {
        return epics.get(id);
    }

    private Subtask findSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    public Integer getTaskCounter() {
        return taskCounter;
    }

    protected void setTaskCounter(Integer taskCounter) {
        this.taskCounter = taskCounter;
    }

    protected Map<Integer, Task> getTasksMap() {
        return tasks;
    }

    protected Map<Integer, Epic> getEpicsMap() {
        return epics;
    }

    protected Map<Integer, Subtask> getSubtasksMap() {
        return subtasks;
    }

    @Override
    public File getSavePath() {
        return null;
    }

}

