package ru.practicum.manager.General;

import ru.practicum.manager.History.HistoryManager;
import ru.practicum.manager.History.InMemoryHistoryManager;
import ru.practicum.manager.Memory.InMemoryTaskManager;

public class Managers {

    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
