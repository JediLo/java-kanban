package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskProgress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    public static final int DEFAULT_ID_TASK = 0;
    private int countTasks = DEFAULT_ID_TASK;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();


    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpicTask(int id) {
        return epics.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    public int addNewTask(Task task) {
        countTasks++;
        task.setTaskID(countTasks);
        task.setTaskProgress(TaskProgress.NEW);
        tasks.put(countTasks, task);
        return countTasks;
    }

    public int addNewEpicTask(Epic task) {
        countTasks++;
        task.setTaskID(countTasks);
        task.setTaskProgress(TaskProgress.NEW);
        epics.put(countTasks, task);
        return countTasks;
    }

    public int addNewSubTask(SubTask task) {
        Epic epic = epics.get(task.getEpicTaskID());
        if (epic == null) {
            return -1;
        } else {
            countTasks++;
            task.setTaskID(countTasks);
            task.setTaskProgress(TaskProgress.NEW);
            subTasks.put(countTasks, task);
            epic.addSubTask(task);
            checkAndReplaceTaskProgress(epic);
            return countTasks;
        }

    }

    public void updateTask(Task newTask) {
        Task task = tasks.get(newTask.getTaskID());
        if (task != null) {
            tasks.put(newTask.getTaskID(), newTask);
        }
    }

    public void updateEpic(Epic newEpic) {
        Epic epic = epics.get(newEpic.getTaskID());
        if (epic != null) {
            epic.setName(newEpic.getName());
            epic.setDescription(newEpic.getDescription());
        }
    }

    public void updateSubtask(SubTask newSubTask) {
        Epic epic = epics.get(newSubTask.getEpicTaskID());
        if (epic != null) {
            for (int idSubTask : epic.getSubTasksID()) {
                if (epic.getTaskID() == subTasks.get(idSubTask).getEpicTaskID()) {
                    subTasks.put(newSubTask.getTaskID(), newSubTask);
                    checkAndReplaceTaskProgress(epic);
                }
            }
        }


    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpicTask(int id) {
        for (SubTask subTask : new ArrayList<>(subTasks.values())) {
            if (subTask.getEpicTaskID() == id) {
                subTasks.remove(subTask.getTaskID());
            }
        }
        epics.remove(id);
    }

    public void deleteSubtask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask == null) {
            return;
        }
        Epic epicToReplace = epics.get(subTask.getEpicTaskID());
        if (epicToReplace != null) {
            epicToReplace.removeSubTask(subTasks.get(id));
            subTasks.remove(id);
            checkAndReplaceTaskProgress(epicToReplace);
        }

    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpicTasks() {
        epics.clear();
        subTasks.clear();

    }

    public void deleteSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubTasks();
            checkAndReplaceTaskProgress(epic);
        }
    }

    public List<Task> getAllTask() {
        return tasks.values().stream().toList();
    }

    public List<Epic> getAllEpic() {
        return epics.values().stream().toList();
    }

    public List<SubTask> getAllSubTask() {
        return subTasks.values().stream().toList();
    }

    private void checkAndReplaceTaskProgress(Epic epic) {
        List<Integer> allSubTasksFromEpic = epic.getSubTasksID();
        if (allSubTasksFromEpic.isEmpty()) {
            epic.setTaskProgress(TaskProgress.NEW);
            return;
        }
        boolean isInProgress = false;
        boolean isDone = true;
        TaskProgress taskProgressToSubTask;
        for (int idSubTaskFromEpic : allSubTasksFromEpic) {
            if (subTasks.get(idSubTaskFromEpic).getEpicTaskID() == epic.getTaskID()) {
                taskProgressToSubTask = subTasks.get(idSubTaskFromEpic).getTaskProgress();
                if (taskProgressToSubTask != TaskProgress.NEW) {
                    isInProgress = true;
                }
                if (taskProgressToSubTask != TaskProgress.DONE) {
                    isDone = false;
                }
            }
        }

        if (isDone) {
            epic.setTaskProgress(TaskProgress.DONE);
        } else if (isInProgress) {
            epic.setTaskProgress(TaskProgress.IN_PROGRESS);

        } else {
            epic.setTaskProgress(TaskProgress.NEW);
        }

    }

}
