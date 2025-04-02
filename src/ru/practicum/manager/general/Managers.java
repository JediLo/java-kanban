package ru.practicum.manager.general;

import ru.practicum.manager.history.HistoryManager;
import ru.practicum.manager.history.InMemoryHistoryManager;
import ru.practicum.manager.memory.InMemoryTaskManager;

public class Managers {

    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
