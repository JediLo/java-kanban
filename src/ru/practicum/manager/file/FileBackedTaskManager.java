package ru.practicum.manager.file;

import ru.practicum.exceptons.EpicNotFoundExceptions;
import ru.practicum.exceptons.ManagerSaveException;
import ru.practicum.manager.memory.InMemoryTaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File fileTasks) {
        FileBackedTaskManager manager = new FileBackedTaskManager(fileTasks);

        if (manager.file.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(manager.file))) {
                String line;
                while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                    Task task = ManagerSCV.getTaskFromString(line);
                    manager.loadTaskFromString(task);
                }
            } catch (IOException e) {
                throw new RuntimeException("Не удалось загрузить данные из файла", e);
            }
        }
        return manager;
    }

    private void saveToSCV(String toSCV, File file) {
        try {
            Files.writeString(Path.of(file.getPath()), toSCV);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось записать файл", e);
        }
    }


    private void save() {
        saveToSCV(ManagerSCV.getSCVFromTasks(getAllTasks()), file);
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
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateSubtask(SubTask newSubTask) {
        super.updateSubtask(newSubTask);
        save();
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

    private void loadTaskFromString(Task task) {
        switch (task) {
            case null -> {
                return;
            }
            case SubTask subtaskToMap -> {
                Epic epic = epics.get(subtaskToMap.getEpicTaskID());
                if (epic == null) {
                    throw new EpicNotFoundExceptions("Не найден epic с ID" + subtaskToMap.getEpicTaskID());
                }
                epic.addSubTask(subtaskToMap);
                subTasks.put(subtaskToMap.getTaskID(), subtaskToMap);

            }
            case Epic epicToMap -> epics.put(epicToMap.getTaskID(), epicToMap);
            default -> tasks.put(task.getTaskID(), task);
        }
        if (task.getTaskID() > countTasks) {
            countTasks = task.getTaskID();
        }
    }

    private List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(getAllTask());
        allTasks.addAll(getAllEpic());
        allTasks.addAll(getAllSubTask());
        return allTasks;
    }
}
