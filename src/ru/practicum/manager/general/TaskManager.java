package ru.practicum.manager.general;

import ru.practicum.model.Epic;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;

import java.util.List;

public interface TaskManager {
    Task getTask(int id);

    Epic getEpicTask(int id);

    SubTask getSubTask(int id);

    int addNewTask(Task task);

    int addNewEpicTask(Epic task);

    int addNewSubTask(SubTask task);

    void updateTask(Task newTask);

    void updateEpic(Epic newEpic);

    void updateSubtask(SubTask newSubTask);

    void deleteTask(int id);

    void deleteEpicTask(int id);

    void deleteSubtask(int id);

    void deleteTasks();

    void deleteEpicTasks();

    void deleteSubTasks();

    List<Task> getAllTask();

    List<Epic> getAllEpic();

    List<SubTask> getAllSubTask();

    List<SubTask> getSubTasksFromEpic(int id);

    List<Task> getHistory();
}
