package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Test //Проверка возможности сохранения и загрузки пустого файла
    void testSaveAndLoadEmptyFile() {
        Assertions.assertEquals(0, taskManager.getSavePath().length());
        int allTasksBeforeSaving = taskManager.getAll().size();
        taskManager.save();
        taskManager.init();
        int allTasksAfterLoading = taskManager.getAll().size();
        Assertions.assertTrue(taskManager.getSavePath().exists());
        Assertions.assertEquals(0, taskManager.getSavePath().length());
        Assertions.assertEquals(allTasksBeforeSaving, allTasksAfterLoading);
    }

    @Test //Проверка возможности сохранения задач разных типов
    void saveSomeDifferentTasks() {
        Integer startedNumberLines = 0;

        try {
            Scanner scanner = new Scanner(taskManager.getSavePath());
            while (scanner.hasNextLine()) {
                scanner.nextLine();
                startedNumberLines++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(0, startedNumberLines);

        taskManager.createTask(new Task("Уборка", "Протереть пыль", TaskStatus.NEW, LocalDateTime.now(), 30));
        taskManager.createTask(new Task( "Отдых", "Посмотреть фильм",
                TaskStatus.NEW, LocalDateTime.now().plusMinutes(35), 120));
        taskManager.createEpic(new Epic("Закончить 6 спринт", "Выполнить все задания курса",
                TaskStatus.DONE, new ArrayList<>(), LocalDateTime.now().plusMinutes(160), 0));
        taskManager.createSubtask(new Subtask("Закончить теорию", "Пройти все уроки спринта",
                TaskStatus.DONE, 2, LocalDateTime.now().plusMinutes(165), 300));
        taskManager.createSubtask(new Subtask("Закончить практику", "Сдать ТЗ 6",
                TaskStatus.NEW, 2, LocalDateTime.now().plusMinutes(475), 300));

        Integer finalNumberLines = 0;
        try {
            Scanner scanner = new Scanner(taskManager.getSavePath());
            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                finalNumberLines++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(5, finalNumberLines);
    }

    @Test //Проверка возможности восстановления задач разного типа
    void loadSomeDifferentTasks() {
        taskManager.createTask(new Task("Уборка", "Протереть пыль", TaskStatus.NEW, LocalDateTime.now(), 30));
        taskManager.createTask(new Task( "Отдых", "Посмотреть фильм",
                TaskStatus.NEW, LocalDateTime.now().plusMinutes(35), 120));
        taskManager.createEpic(new Epic("Закончить 6 спринт", "Выполнить все задания курса",
                TaskStatus.DONE, new ArrayList<>(), LocalDateTime.now().plusMinutes(160), 0));
        taskManager.createSubtask(new Subtask("Закончить теорию", "Пройти все уроки спринта",
                TaskStatus.DONE, 2, LocalDateTime.now().plusMinutes(165), 300));
        taskManager.createSubtask(new Subtask("Закончить практику", "Сдать ТЗ 6",
                TaskStatus.NEW, 2, LocalDateTime.now().plusMinutes(475), 300));

        FileBackedTaskManager newManager = new FileBackedTaskManager(taskManager.getSavePath());
        newManager.init();
        Assertions.assertEquals(5, newManager.getAll().size());
    }

    @Override
    protected FileBackedTaskManager createManager() {
        FileBackedTaskManager manager = null;
        try {
            File tempFile = File.createTempFile("taskInfoTest", ".csv");
            manager = new FileBackedTaskManager(tempFile);
            manager.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return manager;
    }

}
