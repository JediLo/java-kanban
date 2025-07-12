package ru.practicum.manager.memory;

import ru.practicum.exceptons.ManagerSaveException;
import ru.practicum.manager.general.Managers;
import ru.practicum.manager.general.TaskManager;
import ru.practicum.manager.history.HistoryManager;
import ru.practicum.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected int countTasks = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();


    @Override
    public Task getTask(int id) throws ManagerSaveException {
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        history.add(task);
        return tasks.get(id);
    }

    @Override
    public Epic getEpicTask(int id) throws ManagerSaveException {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        history.add(epic);
        return epics.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask == null) {
            return null;
        }
        history.add(subTask);
        return subTasks.get(id);
    }

    @Override
    public int addNewTask(Task task) {
        if (task == null) {
            return -1;
        }
        countTasks++;
        task.setTaskID(countTasks);
        task.setTaskProgress(TaskProgress.NEW);
        task.setTaskType(TaskType.TASK);
        tasks.put(countTasks, task);
        return countTasks;
    }

    @Override
    public int addNewEpicTask(Epic task) {
        if (task == null) {
            return -1;
        }
        countTasks++;
        task.setTaskID(countTasks);
        task.setTaskProgress(TaskProgress.NEW);
        task.setTaskType(TaskType.EPIC);
        epics.put(countTasks, task);
        return countTasks;
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        if (subTask == null) {
            return -1;
        }
        Epic epic = epics.get(subTask.getEpicTaskID());
        if (epic == null) {
            return -1;
        } else {
            countTasks++;
            subTask.setTaskID(countTasks);
            subTask.setTaskProgress(TaskProgress.NEW);
            subTask.setTaskType(TaskType.SUBTASK);
            subTasks.put(countTasks, subTask);
            epic.addSubTask(subTask);
            checkAndReplaceTaskProgress(epic);
            return countTasks;
        }

    }

    @Override
    public void updateTask(Task newTask) {
        if (newTask == null) {
            return;
        }
        Task task = tasks.get(newTask.getTaskID());
        if (task != null) {
            tasks.put(newTask.getTaskID(), newTask);
        }
    }

    @Override
    public void updateEpic(Epic newEpic) {
        if (newEpic == null) {
            return;
        }
        Epic epic = epics.get(newEpic.getTaskID());
        if (epic != null) {
            epic.setName(newEpic.getName());
            epic.setDescription(newEpic.getDescription());
        }
    }

    @Override
    public void updateSubtask(SubTask newSubTask) {
        if (newSubTask == null) {
            return;
        }
        Epic epic = epics.get(newSubTask.getEpicTaskID());
        if (epic == null) {
            return;
        }
        SubTask subTask = subTasks.get(newSubTask.getTaskID());
        if (subTask != null) {

            subTasks.put(newSubTask.getTaskID(), newSubTask);
            checkAndReplaceTaskProgress(epic);
        }

    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return;
        }
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteEpicTask(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        for (int idSubTask : epic.getSubTasksID()) {
            subTasks.remove(idSubTask);
            history.remove(idSubTask);
        }

        epics.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask == null) {
            return;
        }
        Epic epicToReplace = epics.get(subTask.getEpicTaskID());
        if (epicToReplace != null) {
            epicToReplace.removeSubTask(subTask);
            checkAndReplaceTaskProgress(epicToReplace);
        }
        subTasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteTasks() {
        for (Task task : tasks.values()) {
            history.remove(task.getTaskID());
        }
        tasks.clear();
    }

    @Override
    public void deleteEpicTasks() {
        for (Epic epic : epics.values()) {
            history.remove(epic.getTaskID());
        }
        epics.clear();
        for (SubTask subTask : subTasks.values()) {
            history.remove(subTask.getTaskID());
        }
        subTasks.clear();

    }

    @Override
    public void deleteSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            history.remove(subTask.getTaskID());
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubTasks();
            checkAndReplaceTaskProgress(epic);
        }
    }

    @Override
    public List<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<SubTask> getSubTasksFromEpic(int id) {
        List<SubTask> subTasksFromEpic = new ArrayList<>();
        Epic epic = epics.get(id);
        if (epic != null) {
            for (int idSubTask : epic.getSubTasksID()) {
                subTasksFromEpic.add(subTasks.get(idSubTask));
            }
        }
        return subTasksFromEpic;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    private void checkAndReplaceTaskProgress(Epic epic) {
        if (epic == null) {
            return;
        }
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
