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

        taskManager.createTask(new Task(0, "Уборка", "Протереть пыль", TaskStatus.NEW));
        taskManager.createTask(new Task(0, "Отдых", "Посмотреть фильм", TaskStatus.NEW));
        taskManager.createEpic(new Epic(0, "Закончить 6 спринт", "Выполнить все задания курса", TaskStatus.DONE, new ArrayList<>()));
        taskManager.createSubtask(new Subtask(0, "Закончить теорию", "Пройти все уроки спринта", TaskStatus.DONE, 2));
        taskManager.createSubtask(new Subtask(0, "Закончить практику", "Сдать ТЗ 6", TaskStatus.NEW, 2));

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
        taskManager.createTask(new Task(0, "Уборка", "Протереть пыль", TaskStatus.NEW));
        taskManager.createTask(new Task(0, "Отдых", "Посмотреть фильм", TaskStatus.NEW));
        taskManager.createEpic(new Epic(0, "Закончить 6 спринт", "Выполнить все задания курса", TaskStatus.DONE, new ArrayList<>()));
        taskManager.createSubtask(new Subtask(0, "Закончить теорию", "Пройти все уроки спринта", TaskStatus.DONE, 2));
        taskManager.createSubtask(new Subtask(0, "Закончить практику", "Сдать ТЗ 6", TaskStatus.NEW, 2));

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