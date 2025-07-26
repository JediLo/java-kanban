package ru.practicum.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void shouldBeTrueWhenEpicIdMatches() {
        Epic epicFirst = new Epic(1, TaskType.EPIC,"First Epic"
                ,TaskProgress.NEW, "Description first Epic");
        Epic epicSecond = new Epic(1, TaskType.EPIC,"Second Epic"
                ,TaskProgress.NEW, "Description second Epic");
        assertEquals(epicFirst, epicSecond);
    }

}