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

    @Test
    void shouldBeTrueWhenEpicIdMatches() {
        Epic epicFirst = new Epic("First Epic", "Description first Epic");
        Epic epicSecond = new Epic("Second Epic", "Description second Epic");
        epicFirst.setTaskID(1);
        epicSecond.setTaskID(1);
        assertEquals(epicFirst, epicSecond);
    }

    @Test
    void shouldBeTrueWhenSubTaskIdMatches() {
        SubTask subTaskFirst = new SubTask("First SubTask", "Description first SubTask", 1);
        SubTask subTaskSecond = new SubTask("Second SubTask", "Description second SubTask", 1);
        subTaskFirst.setTaskID(1);
        subTaskSecond.setTaskID(1);
        assertEquals(subTaskFirst, subTaskSecond);
    }
}