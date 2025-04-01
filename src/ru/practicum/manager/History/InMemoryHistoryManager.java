package ru.practicum.manager.History;

import ru.practicum.model.Epic;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> historyViews = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null){
            return;
        }
        Task taskToHistory;
        if(task instanceof SubTask subTask){
            taskToHistory = new SubTask(subTask);
        } else if (task instanceof Epic epic) {
             taskToHistory = new Epic(epic);
        } else {
            taskToHistory = new Task(task);
        }

        if (historyViews.size() < 10) {
            historyViews.add(taskToHistory);
        } else {
            historyViews.removeFirst();
            historyViews.add(taskToHistory);
        }
    }

    @Override
    public LinkedList<Task> getHistory() {
        return historyViews;
    }
}
