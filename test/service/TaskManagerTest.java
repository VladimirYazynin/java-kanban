package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;
    Task task;

    protected abstract T createManager();

    @BeforeEach
    void init() {
        taskManager = createManager();
    }

    @AfterEach
    void clear() {
        if (taskManager instanceof FileBackedTaskManager)
            taskManager.getSavePath().delete();
    }

    @Test
        // проверьте, что экземпляры класса model.Task равны друг другу, если равен их id;
    void shouldTaskObjectEqualsIfIdSame() {
        Task task = new Task(1, "Уборка", "Помыть посуду", TaskStatus.NEW, LocalDateTime.now(), 10);
        Task newTask = new Task(1, "Учёба", "Выучить стих", TaskStatus.DONE, LocalDateTime.now().plusMinutes(30), 30);
        Assertions.assertEquals(task, newTask);
    }

    @Test
        // проверьте, что наследники класса model.Task равны друг другу, если равен их id;
    void taskHeirsShouldBeEqualsIfIdSame() {
        Epic epic = new Epic(0, "Переезд", "Выполнить много дел",
                TaskStatus.NEW, new ArrayList<>());
        Epic newEpic = new Epic(0, "Покупки", "Купить хлеб и кофе",
                TaskStatus.IN_PROGRESS, new ArrayList<>());
        Subtask subtask = new Subtask(1, "Собрать вещи",
                "Уложить всё по коробкам", TaskStatus.IN_PROGRESS, 0);
        Subtask newSubtask = new Subtask(1, "Взять выпечку", "Купить слойки",
                TaskStatus.DONE, 1);
        Assertions.assertEquals(epic, newEpic);
        Assertions.assertEquals(subtask, newSubtask);
    }

    @Test
        // проверьте, что объект model.Epic нельзя добавить в самого себя в виде подзадачи;
    void epicCantBeAsEpicSubtask() {
        taskManager.createEpic(new Epic(0, "Переезд", "Выполнить много дел",
                        TaskStatus.NEW, new ArrayList<>() {{
                    add(0);
                }})
        );
        Assertions.assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
        // проверьте, что объект model.Subtask нельзя сделать своим же эпиком;
    void subtaskCantBeAsSubtaskEpic() {
        taskManager.createSubtask(new Subtask(0, "Сбор вещей",
                "Уложить всё в коробки", TaskStatus.IN_PROGRESS, 0));
        Assertions.assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
        // Проверка того, что service.InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    void shouldInMemoryTaskManagerAddTasksAndFindItById() {
        //todo
        Task savedTask = new Task("Уборка", "Протереть пыль", TaskStatus.NEW, LocalDateTime.now(), 30);
        taskManager.createTask(savedTask);
        Assertions.assertEquals(savedTask, taskManager.getAllTasks().get(0));
        Epic savedEpic = new Epic(1, "Мегауборка", "Помыть посуду",
                TaskStatus.NEW, new ArrayList<>(), LocalDateTime.now().plusMinutes(125), 10);
        taskManager.createEpic(savedEpic);
        Assertions.assertEquals(savedEpic, taskManager.getAllEpics().get(0));
        Subtask savedSubtask = new Subtask(2, "Стирка",
                "Постирать веши", TaskStatus.IN_PROGRESS, 1, LocalDateTime.now().plusMinutes(140), 60);
        taskManager.createSubtask(savedSubtask);
        Assertions.assertEquals(savedSubtask, taskManager.getAllSubtasks().get(0));
    }

    @Test
        // Проверка того, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    void generatedIdGoodWorksWithHandleId() {
        Task taskWithoutId = new Task("Уборка", "Убрать снег во дворе", TaskStatus.NEW, LocalDateTime.now(), 120);
        Task taskWithId = new Task(1000, "Мегауборка", "Помыть посуду", TaskStatus.DONE, LocalDateTime.now().plusMinutes(125), 10);
        taskManager.createTask(taskWithoutId);
        taskManager.createTask(taskWithId);
        Assertions.assertNull(taskManager.getTaskById(1000));
        Assertions.assertNotNull(taskManager.getTaskById(0));
        Assertions.assertNotNull(taskManager.getTaskById(1));
        Assertions.assertEquals(2, taskManager.getAll().size());
    }

    @Test
        // Проверка неизменности задачи, после добавления её в менеджер;
    void checkImmutableTaskAfterSendingInMeneger() {
        Task task = new Task(0, "Уборка", "Помыть посуду", TaskStatus.NEW);
        taskManager.createTask(task);
        Assertions.assertEquals(task, taskManager.getTaskById(0));
    }

    @Test
        // Проверка, того, что service.HistoryManager сохраняет в себе актуальную версию задачи, а также добавляет её в конец списка;
    void shouldSavedOldTaskVersionInHistory() {
        LocalDateTime firstTaskStartTime = LocalDateTime.now();
        taskManager.createTask(new Task("Уборка", "Протереть пыль", TaskStatus.NEW, firstTaskStartTime, 30));
        taskManager.createTask(new Task("Отдых", "Посмотреть фильм", TaskStatus.NEW, LocalDateTime.now().plusMinutes(60), 150));
        taskManager.getTaskById(0);
        taskManager.getTaskById(1);
        taskManager.updateTask(new Task(0, "Работа", "Написать тесты", TaskStatus.IN_PROGRESS, firstTaskStartTime, 45));
        taskManager.getTaskById(0);
        Assertions.assertEquals(
                new Task(0, "Работа", "Написать тесты", TaskStatus.IN_PROGRESS, firstTaskStartTime, 45),
                taskManager.getHistory().get(1));
    }

    @Test
        //Проверка, того, что при удалении всех задач они также удаляются из истории
    void shouldDeletedAllTasksFromHistory() {
        taskManager.createTask(new Task("Уборка", "Помыть посуду", TaskStatus.NEW, LocalDateTime.now(), 10));
        taskManager.createTask(new Task("Учёба", "Выучить стих", TaskStatus.DONE, LocalDateTime.now().plusMinutes(30), 30));
        taskManager.createEpic(new Epic("Закончить 6 спринт", "Выполнить все задания курса",
                TaskStatus.DONE, new ArrayList<>(), LocalDateTime.now().plusMinutes(65), 0));
        taskManager.getTaskById(0);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.deleteAllTasks();
        Assertions.assertEquals(1, taskManager.getHistory().size());
    }

    @Test
        // Проверка, того, что при удалении эпиков из истории они удаляются со своими подзадачами
    void shouldDeletedAllEpicsAndTheirSubtasksFromHistory() {
        taskManager.createTask(new Task("Уборка", "Протереть пыль", TaskStatus.NEW, LocalDateTime.now(), 30));
        taskManager.createEpic(new Epic("Закончить 6 спринт", "Выполнить все задания курса",
                TaskStatus.DONE, new ArrayList<>(), LocalDateTime.now().plusMinutes(35), 0));
        taskManager.createSubtask(new Subtask("Закончить теорию", "Пройти все уроки спринта",
                TaskStatus.DONE, 1, LocalDateTime.now().plusMinutes(40), 300));
        taskManager.createSubtask(new Subtask("Закончить практику", "Сдать ТЗ 6",
                TaskStatus.NEW, 1, LocalDateTime.now().plusMinutes(350), 300));
        taskManager.getTaskById(0);
        taskManager.getEpicById(1);
        taskManager.getSubtaskById(2);
        taskManager.getSubtaskById(3);
        taskManager.deleteAllEpics();
        Assertions.assertEquals(1, taskManager.getHistory().size());
    }

    @Test
        // Проверка, того, что при очистке всех подзадач они удаляются из истории
    void shouldDeleteAllSubtasksFromHistory() {
        taskManager.createTask(new Task("Уборка", "Протереть пыль", TaskStatus.NEW, LocalDateTime.now(), 30));
        taskManager.createEpic(new Epic("Закончить 6 спринт", "Выполнить все задания курса",
                TaskStatus.DONE, new ArrayList<>(), LocalDateTime.now().plusMinutes(35), 0));
        taskManager.createSubtask(new Subtask("Закончить теорию", "Пройти все уроки спринта",
                TaskStatus.DONE, 1, LocalDateTime.now().plusMinutes(40), 300));
        taskManager.createSubtask(new Subtask("Закончить практику", "Сдать ТЗ 6",
                TaskStatus.NEW, 1, LocalDateTime.now().plusMinutes(350), 300));
        taskManager.getTaskById(0);
        taskManager.getEpicById(1);
        taskManager.getSubtaskById(2);
        taskManager.getSubtaskById(3);
        taskManager.deleteAllSubtasks();
        Assertions.assertEquals(2, taskManager.getHistory().size());
    }

    @Test
        // Проверка, того что в истории хранятся только последние вызовы задач;
    void shouldnotRepeatInHistory() {
        taskManager.createTask(new Task("Уборка", "Помыть посуду", TaskStatus.NEW, LocalDateTime.now(), 10));
        taskManager.createTask(new Task("Учёба", "Выучить стих", TaskStatus.DONE, LocalDateTime.now().plusMinutes(30), 30));
        taskManager.getTaskById(1);
        taskManager.getTaskById(0);
        taskManager.getTaskById(0);
        taskManager.getTaskById(1);
        Assertions.assertEquals(2, taskManager.getHistory().size());
    }

}
