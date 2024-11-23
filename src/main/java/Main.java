import model.Task;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getFileBackedTaskManager("taskInfo.csv");
        System.out.println(taskManager.getPrioritizedTasks());
        taskManager.createTask(new Task("Дела по дому", "Зайти в магазин", TaskStatus.NEW, LocalDateTime.parse("2024-11-22T10:41:17"), 15));
        taskManager.createTask(new Task("Отдых", "Посмотреть фильм", TaskStatus.NEW, LocalDateTime.parse("2024-11-27T10:41:17"), 15));
        taskManager.createTask(new Task("", "Зайти в магазин", TaskStatus.NEW, LocalDateTime.parse("2024-10-22T10:30:00"), 25));
        System.out.println(taskManager.getPrioritizedTasks());
    }
}
