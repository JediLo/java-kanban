package ru.practicum.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void shouldBeTrueWhenEpicIdMatches() {
        Epic epicFirst = new Epic("First Epic", "Description first Epic");
        Epic epicSecond = new Epic("Second Epic", "Description second Epic");
        epicFirst.setTaskID(1);
        epicSecond.setTaskID(1);
        assertEquals(epicFirst, epicSecond);
    }

}