package ru.practicum.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    final LocalDateTime timeToFirstTask = LocalDateTime.now();
    final LocalDateTime timeToSecondTask = timeToFirstTask.plusMinutes(2);
    final Duration duration = Duration.ofMinutes(1);
    @Test
    void shouldBeTrueWhenTaskIdMatches() {
        Task taskFirst = new Task(1, TaskType.TASK,"First Task"
                ,TaskProgress.NEW, "Description first Task", timeToFirstTask,duration);
        Task taskSecond = new Task(1, TaskType.TASK,"Second Task"
                ,TaskProgress.NEW, "Description second Task",timeToSecondTask,duration);
        assertEquals(taskFirst, taskSecond);
    }
}