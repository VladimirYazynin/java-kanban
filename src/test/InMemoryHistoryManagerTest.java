import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;

    @BeforeEach
    void beforeAll() {
        historyManager = new InMemoryHistoryManager();
    }


    @Test // Проверка работоспособности добавления задачи в список просмотренных
    void addFirstTaskInHistory() {
        historyManager.add(new Task(10, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW));
        Assertions.assertEquals( 1, historyManager.getHistory().size());
    }

    @Test // Проверка того, что при добавлении 11 задачи, самая старая задача будет удалена, а новая добавлена в конец
    void shouldDeleteFirstTaskInHistoryAndAddNewTaskInEnd() {
        historyManager.add(new Task(10, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW));
        historyManager.add(new Task(11, "Сделать уборку", "Помыть пол", TaskStatus.DONE));
        historyManager.add(new Epic(12, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW, new ArrayList<Integer>()));
        historyManager.add(new Epic(12, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW, new ArrayList<Integer>()));
        historyManager.add(new Task(10, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW));
        historyManager.add(new Task(10, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW));
        historyManager.add(new Task(10, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW));
        historyManager.add(new Task(10, "Зайти в магазин", "Купить молоко и хлеб", TaskStatus.NEW));
        historyManager.add(new Task(11, "Сделать уборку", "Помыть пол", TaskStatus.DONE));
        historyManager.add(new Task(11, "Сделать уборку", "Помыть пол", TaskStatus.DONE));
        historyManager.add(new Task(9, "Почитать", "Прочитать 10 страниц", TaskStatus.IN_PROGRESS));
        Assertions.assertEquals(new Task(9, "Почитать", "Прочитать 10 страниц", TaskStatus.IN_PROGRESS), historyManager.getHistory().get(9));
    }

}