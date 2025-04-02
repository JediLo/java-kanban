package ru.practicum.manager.general;

import org.junit.jupiter.api.Test;
import ru.practicum.manager.history.HistoryManager;


import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void shouldNotNullTaskManager(){
        assertNotNull(Managers.getDefault());
    }
    @Test
    void shouldNotNullHistoryManager(){
        assertNotNull(Managers.getDefaultHistory());
    }
    @Test
    void shouldReturnNewTaskManager() {
        TaskManager taskManagerFirst = Managers.getDefault();
        TaskManager taskManagerSecond = Managers.getDefault();
        assertNotSame(taskManagerFirst, taskManagerSecond,
                "Возвращается не новый экземпляр TaskManager");
    }

    @Test
    void shouldReturnNewHistoryManager() {
        HistoryManager historyManagerFirst = Managers.getDefaultHistory();
        HistoryManager historyManagerSecond = Managers.getDefaultHistory();
        assertNotSame(historyManagerFirst, historyManagerSecond,
                "Возвращается не новый экземпляр HistoryManager");
    }

}