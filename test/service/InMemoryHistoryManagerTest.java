package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;

    @BeforeEach
    void beforeAll() {
        historyManager = new InMemoryHistoryManager();
    }


    @Test
        // Проверка работоспособности добавления задачи в список просмотренных
    void add_AddFirstTaskInHistory_ShouldHistorySizeEqual1() {
        historyManager.add(new Task(10, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW));
        Assertions.assertEquals(1, historyManager.getHistory().size());
    }

    @Test
        // Проверка того, что ограничения на уникальные задачи больше нет и при добавлении 11 задачи, она будет добавлена
    void add_SavedMoreThenTenTasks_ShouldHistorySizeEqual11() {
        historyManager.add(new Task(1, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW));
        historyManager.add(new Task(2, "Сделать уборку", "Помыть пол", TaskStatus.DONE));
        historyManager.add(new Epic(3, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW, new ArrayList<Integer>()));
        historyManager.add(new Epic(4, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW, new ArrayList<Integer>()));
        historyManager.add(new Task(5, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW));
        historyManager.add(new Task(6, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW));
        historyManager.add(new Task(7, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW));
        historyManager.add(new Task(8, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW));
        historyManager.add(new Task(9, "Сделать уборку", "Помыть пол", TaskStatus.DONE));
        historyManager.add(new Task(10, "Сделать уборку", "Помыть пол", TaskStatus.DONE));
        historyManager.add(new Task(11, "Почитать", "Прочитать 10 страниц", TaskStatus.IN_PROGRESS));
        Assertions.assertEquals(11, historyManager.getHistory().size());
    }

    @Test
    void remove_DeleteTaskFromDifferentPlacesInHistory_ShouldNotRemoveEpicAndSubtask() {
        Epic epic = new Epic(1, "Закончить 6 спринт", "Выполнить все задания курса",
                TaskStatus.DONE, new ArrayList<>(), LocalDateTime.now().plusMinutes(35), 0);
        Subtask subtask = new Subtask(3, "Закончить теорию", "Пройти все уроки спринта",
                TaskStatus.DONE, 1, LocalDateTime.now().plusMinutes(65), 60);
        historyManager.add(new Task(0, "Уборка", "Протереть пыль", TaskStatus.NEW, LocalDateTime.now(), 30));
        historyManager.add(epic);
        historyManager.add(new Task(2, "Уборка", "Протереть пыль", TaskStatus.NEW, LocalDateTime.now().plusMinutes(40), 20));
        historyManager.add(subtask);
        historyManager.add(new Task(4, "Уборка", "Протереть пыль", TaskStatus.NEW, LocalDateTime.now().plusMinutes(130), 10));
        Assertions.assertEquals(5, historyManager.getHistory().size());
        historyManager.remove(2);
        Assertions.assertEquals(4, historyManager.getHistory().size());
        historyManager.remove(0);
        Assertions.assertEquals(3, historyManager.getHistory().size());
        historyManager.remove(4);
        Assertions.assertEquals(2, historyManager.getHistory().size());
        Assertions.assertTrue(historyManager.getHistory().containsAll(List.of(epic, subtask)));
    }

    @Test
    void add_AddSameTask_ShouldNotDuplicate() {
        historyManager.add(new Task(0, "Уборка", "Протереть пыль", TaskStatus.NEW, LocalDateTime.now(), 30));
        historyManager.add(new Task(0, "Уборка", "Протереть пыль", TaskStatus.NEW, LocalDateTime.now(), 30));
        Assertions.assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void remove_DeleteTaskFromEmptyHistory_ShouldNotThrowException() {
        Assertions.assertEquals(0, historyManager.getHistory().size());
        Assertions.assertDoesNotThrow(() -> historyManager.remove(0));
    }

}