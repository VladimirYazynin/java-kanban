package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;
import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTaskById(Integer id);

    Epic getEpicById(Integer id);

    Subtask getSubtaskById(Integer id);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void updateTaskById(Integer id, Task task);

    void updateEpicId(Integer id, Epic epic);

    void updateSubtaskById(Integer id, Subtask subtask);

    void deleteTaskById(Integer id);

    void deleteEpicById(Integer id);

    void deleteSubtaskById(Integer id);

    List<Task> getAll();

    List<Subtask> getAllSubtasksOfEpicById(Integer id);

    List<Task> getHistory();

    File getSavePath();

    List<Task> getPrioritizedTasks();

    Task findTaskById(Integer id);

    Epic findEpicById(Integer id);

    Subtask findSubtaskById(Integer id);

}
