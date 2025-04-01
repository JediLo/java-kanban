package ru.practicum.manager.History;

import ru.practicum.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    List<Task> getHistory();
}
