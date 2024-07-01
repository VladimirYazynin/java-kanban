import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        taskManager.createTask(new Task(0, "Уборка", "Протереть пыль", TaskStatus.NEW));
        taskManager.createTask(new Task(0, "Отдых", "Посмотреть фильм", TaskStatus.NEW));
        taskManager.createTask(new Epic(0, "Закончить 4 спринт", "Выполнить все задания курса", TaskStatus.DONE, new ArrayList<>()));
        taskManager.createTask(new Subtask(0, "Закончить теорию", "Пройти все уроки спринта", TaskStatus.DONE, 2));
        taskManager.createTask(new Subtask(0, "Закончить практику", "Сдать ТЗ 4", TaskStatus.NEW, 2));
        taskManager.createTask(new Epic(0, "Погулять", "Встретиться с друзьями в парке", TaskStatus.IN_PROGRESS, new ArrayList<>()));
        taskManager.createTask(new Subtask(0, "Организовать встречу", "Позвонить всем друзьям", TaskStatus.DONE, 5));
        System.out.println(taskManager.getAllTasks());

        System.out.println(taskManager.getTaskById(1));
        taskManager.updateTask(new Task(1, "Работа", "Закрыть таску", TaskStatus.DONE));
        System.out.println(taskManager.getTaskById(1));

        System.out.println(taskManager.getTaskById(2));
        taskManager.updateTask(new Subtask(3, "Закончить теорию", "Пройти все уроки спринта", TaskStatus.NEW, 2));
        System.out.println(taskManager.getTaskById(2));
        taskManager.updateTask(new Subtask(3, "Закончить теорию", "Пройти все уроки спринта", TaskStatus.DONE, 2));
        taskManager.updateTask(new Subtask(4, "Закончить теорию", "Пройти все уроки спринта", TaskStatus.DONE, 2));
        System.out.println(taskManager.getTaskById(2));

        taskManager.deleteById(0);
        System.out.println(taskManager.getAllTasks());

        taskManager.deleteById(2);
        System.out.println(taskManager.getAllTasks());

        System.out.println(taskManager.getAllSubasksOfEpicById(5));

        taskManager.deleteAllTasks();
        System.out.println(taskManager.getAllTasks());
    }
}
