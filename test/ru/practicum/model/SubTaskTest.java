package ru.practicum.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    LocalDateTime timeToFirstTask = LocalDateTime.now();
    LocalDateTime timeToSecondTask = timeToFirstTask.plusMinutes(2);
    Duration duration = Duration.ofMinutes(1);
    @Test
    void shouldBeTrueWhenSubTaskIdMatches() {
        SubTask subTaskFirst = new SubTask(1, TaskType.SUBTASK, "First SubTask"
                ,TaskProgress.NEW, "Description first SubTask", 1,timeToFirstTask,duration);
        SubTask subTaskSecond = new SubTask(1, TaskType.SUBTASK,"Second SubTask"
                ,TaskProgress.NEW, "Description second SubTask", 1,timeToSecondTask,duration);
        assertEquals(subTaskFirst, subTaskSecond);
    }

}