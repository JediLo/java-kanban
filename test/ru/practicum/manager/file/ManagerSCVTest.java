package ru.practicum.manager.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagerSCVTest {
    private static final LocalDateTime timeToTask = LocalDateTime.now();
    private static final Duration duration = Duration.ofMinutes(1);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm_dd_MM_yyyy");

    @Test
    void shouldBeSCVStringFromTask() {
        Task task = new Task(1, TaskType.TASK, "Name", TaskProgress.IN_PROGRESS,
                "description", timeToTask, duration);
        List<Task> taskList = new ArrayList<>();
        taskList.add(task);
        String stringSCV = ManagerSCV.getSCVFromTasks(taskList);
        String stringResult = ManagerSCV.title + System.lineSeparator()
                + "1,TASK,Name,IN_PROGRESS,description, ," + timeToTask.format(formatter) + "," + duration.toNanos();
        ;
        Assertions.assertEquals(stringSCV, stringResult);
    }

    @Test
    void shouldBeNullFromSCVTitle() {
        Task stringSCV = ManagerSCV.getTaskFromString(ManagerSCV.title);
        assertNull(stringSCV);
    }

    @Test
    void shouldBeTaskFromSCVString() {
        Task task = new Task(1, TaskType.TASK, "Name", TaskProgress.IN_PROGRESS,
                "description", timeToTask, duration);
        Task stringSCV = ManagerSCV.getTaskFromString("1,TASK,Name,IN_PROGRESS,description, ,"
                + timeToTask.format(formatter) + "," + duration.toNanos());
        Assertions.assertEquals(task, stringSCV);
    }

    @Test
    void shouldBeEpicFromSCVString() {
        Epic epic = new Epic(1, TaskType.EPIC, "Name", TaskProgress.IN_PROGRESS, "description");
        Epic stringSCV = (Epic) ManagerSCV.getTaskFromString("1,EPIC,Name,IN_PROGRESS,description, ,"
                + timeToTask.format(formatter) + "," + duration.toNanos());

        Assertions.assertEquals(epic, stringSCV);
    }

    @Test
    void shouldBeSubTaskFromSCVString() {
        SubTask subTask = new SubTask(1, TaskType.SUBTASK, "Name", TaskProgress.IN_PROGRESS, "description", 2
                , timeToTask, duration);
        SubTask stringSCV = (SubTask) ManagerSCV
                .getTaskFromString("1,SUBTASK,Name,IN_PROGRESS,description,2,"
                        + timeToTask.format(formatter) + "," + duration.toNanos());
        Assertions.assertEquals(subTask, stringSCV);
    }
}