import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class InMemoryTaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test // проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    void shouldTaskObjectEqualsIfIdSame() {
        Task task = new Task(1, "Уборка", "Помыть посуду", TaskStatus.NEW);
        Task newTask = new Task(1, "Учёба", "Выучить стих", TaskStatus.DONE);
        Assertions.assertEquals(task, newTask);
    }

    @Test // проверьте, что наследники класса Task равны друг другу, если равен их id;
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

    @Test // проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
    void epicCantBeAsEpicSubtask() {
        taskManager.createEpic(new Epic(0, "Переезд", "Выполнить много дел",
                TaskStatus.NEW, new ArrayList<>() {{
                    add(0);
        }}));
        Assertions.assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test // проверьте, что объект Subtask нельзя сделать своим же эпиком;
    void subtaskCantBeAsSubtaskEpic() {
        taskManager.createSubtask(new Subtask(0, "Сбор вещей",
                "Уложить всё в коробки", TaskStatus.IN_PROGRESS, 0));
        Assertions.assertEquals( 0, taskManager.getAllSubtasks().size());
    }

    @Test // Проверка того, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    void shouldInMemoryTaskManagerAddTasksAndFindItById() {
        Task savedTask = new Task(0, "Уборка", "Протереть пыль", TaskStatus.NEW);
        taskManager.createTask(savedTask);
        Assertions.assertEquals(savedTask, taskManager.getAllTasks().get(0));
        Epic savedEpic = new Epic(1, "Мегауборка", "Помыть посуду", TaskStatus.NEW, new ArrayList<>());
        taskManager.createEpic(savedEpic);
        Assertions.assertEquals(savedEpic, taskManager.getEpicById(savedEpic.getId()));
        Subtask savedSubtask = new Subtask(2, "Стирка",
                "Постирать веши", TaskStatus.IN_PROGRESS, 1);
        taskManager.createSubtask(savedSubtask);
        Assertions.assertEquals(savedSubtask, taskManager.getSubtaskById(savedSubtask.getId()));
    }

    @Test // Проверка того, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    void generatedIdGoodWorksWithHandleId() {
        Task task = new Task(1000, "Уборка", "Убрать снег во дворе", TaskStatus.NEW);
        taskManager.createTask(task);
        Assertions.assertNull(taskManager.getTaskById(1000));
        Task savedTask = taskManager.getAllTasks().get(0);
        savedTask.setId(1000);
        taskManager.updateTask(savedTask);
        Assertions.assertNull(taskManager.getTaskById(1000));
    }

    @Test // Проверка неизменности задачи, после добавления её в менеджер;
    void checkImmutableTaskAfterSendingInMeneger() {
        Task task = new Task(0, "Уборка", "Помыть посуду", TaskStatus.NEW);
        taskManager.createTask(task);
        Assertions.assertEquals(task, taskManager.getTaskById(0));
    }

    @Test // Проверка, того что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных;
    void shouldSavedOldTaskVersionInHistory() {
        taskManager.createTask(new Task(0, "Уборка", "Протереть пыль", TaskStatus.NEW));
        taskManager.createTask(new Task(0, "Отдых", "Посмотреть фильм", TaskStatus.NEW));
        taskManager.getTaskById(0);
        taskManager.getTaskById(1);
        taskManager.updateTask(new Task(0, "Работа", "Написать тесты", TaskStatus.IN_PROGRESS));
        taskManager.getTaskById(0);
        Assertions.assertEquals(new Task(0, "Уборка", "Протереть пыль", TaskStatus.NEW),
                taskManager.getHistory().get(0));
    }
}