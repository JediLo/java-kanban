package ru.practicum.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    @Test
    void shouldBeTrueWhenSubTaskIdMatches() {
        SubTask subTaskFirst = new SubTask("First SubTask", "Description first SubTask", 1);
        SubTask subTaskSecond = new SubTask("Second SubTask", "Description second SubTask", 1);
        subTaskFirst.setTaskID(1);
        subTaskSecond.setTaskID(1);
        assertEquals(subTaskFirst, subTaskSecond);
    }

}