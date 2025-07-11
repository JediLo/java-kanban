package ru.practicum.manager.file;

import ru.practicum.exceptons.ManagerSaveException;
import ru.practicum.manager.memory.InMemoryTaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public static FileBackedTaskManager loadFromFile(File fileTasks) {
        FileBackedTaskManager manager = new FileBackedTaskManager();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileTasks))) {
            String line;
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                Task task = ManagerSCV.getTaskFromString(line);
                manager.loadTaskFromString(task);
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить данные из файла", e);
        }
        return manager;
    }

    void saveToSCV(String toSCV, String file) {
        try {
            Files.writeString(Path.of(file), toSCV);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось записать файл", e);
        }
    }


    void save() {
        String fileNameTasks = "tasks.csv";
        saveToSCV(ManagerSCV.getSCVFromTasks(getAllTasks()), fileNameTasks);
    }


    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        if (task != null) {
            save();
        }
        return task;
    }

    @Override
    public Epic getEpicTask(int id) {
        Epic epic = super.getEpicTask(id);
        if (epic != null) {
            save();
        }
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        if (subTask != null) {
            save();
        }
        return subTask;
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        if (id > 0) {
            save();
        }
        return id;
    }

    @Override
    public int addNewEpicTask(Epic task) {
        int id = super.addNewEpicTask(task);
        if (id > 0) {
            save();
        }
        return id;
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        int id = super.addNewSubTask(subTask);
        if (id > 0) {
            save();
        }
        return id;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpicTask(int id) {
        super.deleteEpicTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpicTasks() {
        super.deleteEpicTasks();
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }
}
