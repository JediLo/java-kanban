package ru.practicum.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldBeTrueWhenTaskIdMatches() {
        Task taskFirst = new Task("First Task", "Description first Task");
        Task taskSecond = new Task("Second Task", "Description second Task");
        taskFirst.setTaskID(1);
        taskSecond.setTaskID(1);
        assertEquals(taskFirst, taskSecond);
    }
}