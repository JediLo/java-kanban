package ru.practicum.manager.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void shouldBeloadFromFile() throws IOException {
        // Подготавливаем Task
        Task task = new Task(1, TaskType.TASK, "Name", TaskProgress.NEW, "description");
        // Формируем строку, которая должна быть в файле
        String line = ManagerSCV.getTitle() + System.lineSeparator() + "1,TASK,Name,NEW,description, ";
        // Создаем файл
        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        // Записываем подготовленную строку в файл
        Files.writeString(tempFile.toPath(), line);
        // Создаем менеджер с подготовленным файлом
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Получаем Task из нашего менеджера
        Task taskFromFile = fileBackedTaskManager.getTask(task.getTaskID());

        equalsTasks(task, taskFromFile);
    }


    @Test
    void shouldBeSaveToFile() throws IOException {
        // Подготавливаем Task
        Task task = new Task(1, TaskType.TASK, "Name", TaskProgress.NEW, "description");
        // Формируем строку, которая должна быть в файле
        String line = ManagerSCV.getTitle() + System.lineSeparator() + "1,TASK,Name,NEW,description, ";
        // Создаем файл и менеджер с этим файлом
        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        // Создаем менеджер с подготовленным файлом
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);
        // Добавляем подготовленный Task в наш менеджер
        fileBackedTaskManager.addNewTask(task);

        Assertions.assertEquals(line, Files.readString(tempFile.toPath()));
    }

    @Test
    void shouldBeLoadingFromVoidFile() throws IOException {
        // Создаем файл и менеджер с этим файлом
        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);

        Assertions.assertTrue(fileBackedTaskManager.getAllTask().isEmpty());
        Assertions.assertTrue(fileBackedTaskManager.getAllSubTask().isEmpty());
        Assertions.assertTrue(fileBackedTaskManager.getAllEpic().isEmpty());
    }

    @Test
    void shouldSaveMultipleTasksToFile() throws IOException {
        // Подготавливаем Tasks
        Task task = new Task(1, TaskType.TASK, "Name task", TaskProgress.NEW, "description task");
        Epic epic = new Epic(2, TaskType.EPIC, "Name epic", TaskProgress.NEW, "description epic");
        SubTask subTask = new SubTask(3, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "description SubTask", 2);
        // Формируем строку, которая должна быть в файле
        StringBuilder sb = new StringBuilder();
        sb.append(ManagerSCV.getTitle()).append(System.lineSeparator());
        sb.append("1,TASK,Name task,NEW,description task, ").append(System.lineSeparator());
        sb.append("2,EPIC,Name epic,NEW,description epic, ").append(System.lineSeparator());
        sb.append("3,SUBTASK,Name SubTask,NEW,description SubTask,2");
        // Создаем файл и менеджер с этим файлом
        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);
        // Добавляем подготовленные Tasks в наш менеджер
        fileBackedTaskManager.addNewTask(task);
        fileBackedTaskManager.addNewEpicTask(epic);
        fileBackedTaskManager.addNewSubTask(subTask);

        Assertions.assertEquals(sb.toString(), Files.readString(tempFile.toPath()));

    }

    @Test
    void shouldLoadMultipleTasksFromFile() throws IOException {
        // Подготавливаем Tasks
        Task task = new Task(1, TaskType.TASK, "Name task", TaskProgress.NEW, "description task");
        Epic epic = new Epic(2, TaskType.EPIC, "Name epic", TaskProgress.NEW, "description epic");
        SubTask subTask = new SubTask(3, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "description SubTask", 2);
        // Формируем строку, которая должна быть в файле
        StringBuilder sb = new StringBuilder();
        sb.append(ManagerSCV.getTitle()).append(System.lineSeparator());
        sb.append("1,TASK,Name task,NEW,description task, ").append(System.lineSeparator());
        sb.append("2,EPIC,Name epic,NEW,description epic, ").append(System.lineSeparator());
        sb.append("3,SUBTASK,Name SubTask,NEW,description SubTask,2");
        // Создаем файл
        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        // Записываем подготовленную строку в файл
        Files.writeString(tempFile.toPath(), sb.toString());
        // Создаем менеджер с подготовленным файлом
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Получаем Tasks из нашего менеджера
        Task taskFromManager = fileBackedTaskManager.getTask(task.getTaskID());
        Epic epicFromManager = fileBackedTaskManager.getEpicTask(epic.getTaskID());
        SubTask subTaskFromManager = fileBackedTaskManager.getSubTask(subTask.getTaskID());
        equalsTasks(task, taskFromManager);
        equalsTasks(epic, epicFromManager);
        equalsSubTasks(subTask, subTaskFromManager);

    }

    @Test
    void shouldBeSaveVoidFile() throws IOException {
        // Подготавливаем Task
        Task task = new Task(1, TaskType.TASK, "Name", TaskProgress.NEW, "description");
        // Формируем строку, которая должна быть в файле
        String line = ManagerSCV.getTitle();
        // Создаем файл и менеджер с этим файлом
        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        // Создаем менеджер с подготовленным файлом
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);
        // Добавляем подготовленный Task в наш менеджер
        fileBackedTaskManager.addNewTask(task);
        // Удаляем нашу задачу
        fileBackedTaskManager.deleteTask(task.getTaskID());

        Assertions.assertEquals(line, Files.readString(tempFile.toPath()));
    }

    @Test
    void shouldBeUpdateFileWhenUpdateTaskInTaskManager() throws IOException {
        // Подготавливаем Task
        Task task = new Task(1, TaskType.TASK, "Name", TaskProgress.NEW, "description");
        // Подготавливаем измененную Task
        Task updateTask = new Task(1, TaskType.TASK, "Update Task"
                , TaskProgress.IN_PROGRESS, "Update Description");
        // Формируем строки, которые должна быть в файле до и после обновления
        String line = ManagerSCV.getTitle() + System.lineSeparator() + "1,TASK,Name,NEW,description, ";
        String lineUpdate = ManagerSCV.getTitle() + System.lineSeparator() + "1,TASK,Update Task,IN_PROGRESS,Update Description, ";
        // Создаем файл и менеджер с этим файлом
        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);
        // Добавляем подготовленный Task в наш менеджер
        fileBackedTaskManager.addNewTask(task);
        //Проверяем строку до обновления
        Assertions.assertEquals(line, Files.readString(tempFile.toPath()));
        // Обновляем нашу задачу в менеджере
        fileBackedTaskManager.updateTask(updateTask);
        //Проверяем строку после обновления
        Assertions.assertEquals(lineUpdate, Files.readString(tempFile.toPath()));
    }

    private static void equalsTasks(Task expected, Task actual) {
        Assertions.assertEquals(expected.getTaskID(), actual.getTaskID());
        Assertions.assertEquals(expected.getType(), actual.getType());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getTaskProgress(), actual.getTaskProgress());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
    }

    private static void equalsSubTasks(SubTask expected, SubTask actual) {
        equalsTasks(expected, actual);
        Assertions.assertEquals(expected.getEpicTaskID(), actual.getEpicTaskID());
    }
}