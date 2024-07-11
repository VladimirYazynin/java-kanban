import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        taskManager.createTask(new Task(0, "Уборка", "Протереть пыль", TaskStatus.NEW));
        taskManager.createTask(new Task(0, "Отдых", "Посмотреть фильм", TaskStatus.NEW));
        taskManager.createEpic(new Epic(0, "Закончить 4 спринт", "Выполнить все задания курса", TaskStatus.DONE, new ArrayList<>()));
        taskManager.createSubtask(new Subtask(0, "Закончить теорию", "Пройти все уроки спринта", TaskStatus.DONE, 2));
        taskManager.createSubtask(new Subtask(0, "Закончить практику", "Сдать ТЗ 4", TaskStatus.NEW, 2));
        taskManager.createEpic(new Epic(0, "Погулять", "Встретиться с друзьями в парке", TaskStatus.IN_PROGRESS, new ArrayList<>()));
        taskManager.createSubtask(new Subtask(0, "Организовать встречу", "Позвонить всем друзьям", TaskStatus.DONE, 5));
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println(taskManager.getTaskById(1));
        taskManager.updateTask(new Task(1, "Работа", "Закрыть таску", TaskStatus.DONE));
        System.out.println(taskManager.getTaskById(1));

        System.out.println(taskManager.getEpicById(2));
        taskManager.updateEpic(new Epic(2, "Закончить 4 спринт", "Выполнить все задания курса", TaskStatus.DONE, new ArrayList<>(Arrays.asList(3, 4))));
        System.out.println(taskManager.getEpicById(2));
        taskManager.updateSubtask(new Subtask(3, "Закончить теорию", "Пройти все уроки спринта", TaskStatus.NEW, 2));
        System.out.println(taskManager.getEpicById(2));
        taskManager.updateSubtask(new Subtask(3, "Закончить теорию", "Пройти все уроки спринта", TaskStatus.DONE, 2));
        taskManager.updateSubtask(new Subtask(4, "Закончить теорию", "Пройти все уроки спринта", TaskStatus.DONE, 2));
        System.out.println(taskManager.getEpicById(2));

        taskManager.deleteTaskById(0);
        System.out.println(taskManager.getAllTasks());

        taskManager.deleteEpicById(2);
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println(taskManager.getAllSubtasksOfEpicById(5));


        taskManager.deleteSubtaskById(6);
        System.out.println(taskManager.getAllSubtasksOfEpicById(5));

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println(taskManager.getHistory());
    }
}
