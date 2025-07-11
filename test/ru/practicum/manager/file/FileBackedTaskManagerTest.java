package ru.practicum.manager.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Task;
import ru.practicum.model.TaskProgress;
import ru.practicum.model.TaskType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void shouldBeloadFromFile() throws IOException {

        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        String line = "1,TASK,Name,IN_PROGRESS,description, ";
        Task task = new Task(1, TaskType.TASK, "Name", TaskProgress.IN_PROGRESS, "description");
        Files.writeString(tempFile.toPath(), line);

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        Task taskFromFile = fileBackedTaskManager.getTask(1);
        Assertions.assertEquals(task, taskFromFile);
    }


    @Test
    void shouldBeSaveToFile() throws IOException {
        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        String line = "1,TASK,Name,IN_PROGRESS,description, ";
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();
        fileBackedTaskManager.saveToSCV(line, tempFile.getPath());

        Assertions.assertEquals(line, Files.readString(tempFile.toPath()));
    }
}