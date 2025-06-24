package ru.practicum.manager.history;

import ru.practicum.model.Epic;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> historyViews = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        Task taskToHistory;
        if (task instanceof SubTask subTask) {
            taskToHistory = new SubTask(subTask);
        } else if (task instanceof Epic epic) {
            taskToHistory = new Epic(epic);
        } else {
            taskToHistory = new Task(task);
        }

        historyViews.add(taskToHistory.getTaskID(), taskToHistory);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyViews.getTasks();
    }

    @Override
    public LinkedList<Task> getHistory() {
        return new LinkedList<>(historyViews);
    }
}
