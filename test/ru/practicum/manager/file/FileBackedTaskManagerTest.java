package ru.practicum.manager.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.exceptons.EpicNotFoundExceptions;
import ru.practicum.exceptons.ManagerSaveException;
import ru.practicum.manager.TaskManagerTest;
import ru.practicum.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    final LocalDateTime timeToFirstTask = LocalDateTime.now();
    final LocalDateTime timeToSecondTask = timeToFirstTask.plusMinutes(2);
    final Duration duration = Duration.ofMinutes(1);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm_dd_MM_yyyy");

    @Test
    void shouldBeloadFromFile() throws IOException {
        // Подготавливаем Task
        Task task = new Task(1, TaskType.TASK, "Name", TaskProgress.NEW,
                "description", timeToFirstTask, duration);
        // Формируем строку, которая должна быть в файле
        String line = ManagerSCV.title + System.lineSeparator()
                + "1,TASK,Name,NEW,description, ," + timeToFirstTask.format(formatter) + "," + duration.toNanos();
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
        Task task = new Task(1, TaskType.TASK, "Name", TaskProgress.NEW,
                "description", timeToFirstTask, duration);
        // Формируем строку, которая должна быть в файле
        String line = ManagerSCV.title + System.lineSeparator() + "1,TASK,Name,NEW,description, ,"
                + timeToFirstTask.format(formatter) + "," + duration.toNanos();
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
        Task task = new Task(1, TaskType.TASK, "Name task", TaskProgress.NEW,
                "description task", timeToFirstTask, duration);
        Epic epic = new Epic(2, TaskType.EPIC, "Name epic", TaskProgress.NEW,
                "description epic");
        SubTask subTask = new SubTask(3, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "description SubTask", 2, timeToSecondTask, duration);
        // Формируем строку, которая должна быть в файле
        StringBuilder sb = new StringBuilder();
        sb.append(ManagerSCV.title).append(System.lineSeparator());
        sb.append("1,TASK,Name task,NEW,description task, ,")
                .append(timeToFirstTask.format(formatter))
                .append(",")
                .append(duration.toNanos()).append(System.lineSeparator());
        sb.append("2,EPIC,Name epic,NEW,description epic, ,").append(timeToSecondTask.format(formatter))
                .append(",").append(duration.toNanos()).append(System.lineSeparator());
        sb.append("3,SUBTASK,Name SubTask,NEW,description SubTask,2,")
                .append(timeToSecondTask.format(formatter))
                .append(",").append(duration.toNanos());
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
    void shouldThrowExceptionWhenSubTaskReferencesMissingEpic() {
        SubTask subTask = new SubTask(1, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "description SubTask", 2,
                timeToSecondTask, duration);
        Assertions.assertThrows(EpicNotFoundExceptions.class, () -> {
            manager.loadTaskFromString(subTask);
        });
    }

    @Test
    void shouldThrowManagerSaveExceptionWhenFileIsNotWritable() {
        String content = "Test content";
        File invalidFile = new File("/root/error.csv");

        Assertions.assertThrows(ManagerSaveException.class, () -> {
            manager.saveToSCV(content, invalidFile);
        });
    }

    @Test
    void shouldLoadMultipleTasksFromFile() throws IOException {
        // Подготавливаем Tasks
        Task task = new Task(1, TaskType.TASK, "Name task", TaskProgress.NEW,
                "description task", timeToFirstTask, duration);
        Epic epic = new Epic(2, TaskType.EPIC, "Name epic", TaskProgress.NEW, "description epic");
        SubTask subTask = new SubTask(3, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "description SubTask", epic.getTaskID(), timeToSecondTask, duration);
        epic.addSubTask(subTask);
        // Обновим время у Epic
        epic.updateTimesEpic(timeToSecondTask, duration, timeToSecondTask.plus(duration));
        // Формируем строку, которая должна быть в файле
        StringBuilder sb = new StringBuilder();
        sb.append(ManagerSCV.title).append(System.lineSeparator());

        sb.append("1,TASK,Name task,NEW,description task, ,")
                .append(timeToFirstTask.format(formatter))
                .append(",").append(duration.toNanos()).append(System.lineSeparator());
        sb.append("2,EPIC,Name epic,NEW,description epic, , , ").append(System.lineSeparator());
        sb.append("3,SUBTASK,Name SubTask,NEW,description SubTask,2,")
                .append(timeToSecondTask.format(formatter))
                .append(",").append(duration.toNanos());
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
        equalsEpic(epic, epicFromManager);
        equalsSubTasks(subTask, subTaskFromManager);


    }

    @Test
    void shouldLoadPrioritizedTasksFromFile() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        LocalDateTime plusOneMinutes = now.plusMinutes(1);
        LocalDateTime plusTwoMinutes = now.plusMinutes(2);
        LocalDateTime plusThreeMinutes = now.plusMinutes(3);
        LocalDateTime plusFourMinutes = now.plusMinutes(4);
        LocalDateTime plusFiveMinutes = now.plusMinutes(5);
        // Создаем разные задачи
        Task firstTaskPlusOneMinutes = new Task(1, TaskType.TASK,
                "Name firstTaskPlusOneMinutes",
                TaskProgress.NEW, "Description firstTaskPlusOneMinutes",
                plusOneMinutes, oneMinutes);
        Task secondTaskNow = new Task(2, TaskType.TASK, "Name secondTaskNow",
                TaskProgress.NEW, "Description secondTaskNow",
                now, oneMinutes);
        Task thirdTaskPlusThreeMinutes = new Task(3, TaskType.TASK,
                "Name thirdTaskPlusThreeMinutes",
                TaskProgress.NEW, "Description thirdTaskPlusThreeMinutes",
                plusThreeMinutes, oneMinutes);
        Epic epic = new Epic(4, TaskType.EPIC, "Name Epic",
                TaskProgress.NEW, "Description Epic");
        SubTask firstSubTaskPlusFiveMinutes = new SubTask(5, TaskType.SUBTASK,
                "Name firstSubTaskPlusFiveMinutes",
                TaskProgress.NEW, "Description firstSubTaskPlusFiveMinutes", epic.getTaskID(),
                plusFiveMinutes, oneMinutes);
        SubTask secondSubTaskPlusFourMinutes = new SubTask(6, TaskType.SUBTASK,
                "Name secondSubTaskPlusFourMinutes",
                TaskProgress.NEW, "Description secondSubTaskPlusFourMinutes", epic.getTaskID(),
                plusFourMinutes, oneMinutes);
        SubTask thirdSubTaskPlusTwoMinutes = new SubTask(7, TaskType.SUBTASK,
                "Name thirdSubTaskPlusTwoMinutes",
                TaskProgress.NEW, "Description thirdSubTaskPlusTwoMinutes", epic.getTaskID(),
                plusTwoMinutes, oneMinutes);
        // Создаем строку, для записи в файл
        List<Task> tasksToSCV = new ArrayList<>();
        tasksToSCV.add(firstTaskPlusOneMinutes);
        tasksToSCV.add(secondTaskNow);
        tasksToSCV.add(thirdTaskPlusThreeMinutes);
        tasksToSCV.add(epic);
        tasksToSCV.add(firstSubTaskPlusFiveMinutes);
        tasksToSCV.add(secondSubTaskPlusFourMinutes);
        tasksToSCV.add(thirdSubTaskPlusTwoMinutes);

        String stringSCV = ManagerSCV.getSCVFromTasks(tasksToSCV);
        // Создаем файл
        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        // Записываем подготовленную строку в файл
        Files.writeString(tempFile.toPath(), stringSCV);
        // Создаем менеджер с подготовленным файлом
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        // Получаем PrioritizedTasks из нашего менеджера
        List<Task> sortedSavedTasks = fileBackedTaskManager.getPrioritizedTasks();


        // Сравниваем задачи от самой ранней к самой поздней
        equalsTasks(sortedSavedTasks.getFirst(), secondTaskNow);
        equalsTasks(sortedSavedTasks.get(1), firstTaskPlusOneMinutes);
        equalsTasks(sortedSavedTasks.get(2), thirdSubTaskPlusTwoMinutes);
        equalsTasks(sortedSavedTasks.get(3), thirdTaskPlusThreeMinutes);
        equalsTasks(sortedSavedTasks.get(4), secondSubTaskPlusFourMinutes);
        equalsTasks(sortedSavedTasks.getLast(), firstSubTaskPlusFiveMinutes);


    }

    @Test
    void shouldBeSaveVoidFile() throws IOException {
        // Подготавливаем Task
        Task task = new Task(1, TaskType.TASK, "Name", TaskProgress.NEW,
                "description", timeToFirstTask, duration);
        // Формируем строку, которая должна быть в файле
        String line = ManagerSCV.title;
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
        Task task = new Task(1, TaskType.TASK, "Name", TaskProgress.NEW,
                "description", timeToFirstTask, duration);
        // Подготавливаем измененную Task
        Task updateTask = new Task(1, TaskType.TASK, "Update Task"
                , TaskProgress.IN_PROGRESS, "Update Description", timeToSecondTask, duration);
        // Формируем строки, которые должна быть в файле до и после обновления
        String line = ManagerSCV.title + System.lineSeparator() + "1,TASK,Name,NEW,description, ,"
                + timeToFirstTask.format(formatter) + "," + duration.toNanos();
        String lineUpdate = ManagerSCV.title
                + System.lineSeparator()
                + "1,TASK,Update Task,IN_PROGRESS,Update Description, ,"
                + timeToSecondTask.format(formatter) + "," + duration.toNanos();
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


    @Override
    protected FileBackedTaskManager createManager() throws IOException {
        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        return new FileBackedTaskManager(tempFile);
    }
}