package ru.practicum.manager.general;

import ru.practicum.manager.file.FileBackedTaskManager;
import ru.practicum.manager.history.HistoryManager;
import ru.practicum.manager.history.InMemoryHistoryManager;

import java.io.File;


public class Managers {

    public static FileBackedTaskManager getDefault() {
        File fileTask = new File("tasks.csv");
        if (fileTask.exists()) {
            return FileBackedTaskManager.loadFromFile(fileTask);
        }
        return new FileBackedTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
