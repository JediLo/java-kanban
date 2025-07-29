package ru.practicum.manager.memory;

import ru.practicum.manager.general.Managers;
import ru.practicum.manager.general.TaskManager;
import ru.practicum.manager.history.HistoryManager;
import ru.practicum.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final TreeSet<Task> sortedTasksToTime = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private final HistoryManager history = Managers.getDefaultHistory();
    protected int countTasks = 0;

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        history.add(task);
        return new Task(tasks.get(id));
    }

    @Override
    public Epic getEpicTask(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        history.add(epic);
        return new Epic(epics.get(id));
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask == null) {
            return null;
        }
        history.add(subTask);
        return new SubTask(subTasks.get(id));
    }

    @Override
    public int addNewTask(Task task) {
        if (task == null) {
            return -1;
        }
        Task newTask = new Task(task);
        if (checkTheCrossingTimeTask(newTask)) {
            return -1;
        }
        countTasks++;
        newTask.setTaskID(countTasks);
        newTask.setTaskProgress(TaskProgress.NEW);
        newTask.setTaskType(TaskType.TASK);
        tasks.put(countTasks, newTask);
        sortedTasksToTime.add(newTask);
        return countTasks;
    }

    @Override
    public int addNewEpicTask(Epic epic) {
        if (epic == null) {
            return -1;
        }
        Epic newEpic = new Epic(epic);
        countTasks++;
        newEpic.setTaskID(countTasks);
        newEpic.setTaskProgress(TaskProgress.NEW);
        newEpic.setTaskType(TaskType.EPIC);
        epics.put(countTasks, newEpic);
        return countTasks;
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        if (subTask == null) {
            return -1;
        }
        if (checkTheCrossingTimeTask(subTask)) {
            return -1;
        }
        Epic epic = epics.get(subTask.getEpicTaskID());
        if (epic == null) {
            return -1;
        } else {
            SubTask newSubTask = new SubTask(subTask);
            countTasks++;
            newSubTask.setTaskID(countTasks);
            newSubTask.setTaskProgress(TaskProgress.NEW);
            newSubTask.setTaskType(TaskType.SUBTASK);
            subTasks.put(countTasks, newSubTask);
            sortedTasksToTime.add(newSubTask);
            epic.addSubTask(newSubTask);
            checkAndReplaceTaskProgress(epic);
            checkAndReplaceEpicTaskTime(epic);
            return countTasks;
        }

    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        Task oldTask = tasks.get(task.getTaskID());
        if (oldTask == null) {
            return;
        }
        Task newTask = new Task(task);
        if (checkTheCrossingTimeTask(newTask)) {
            return;
        }
        tasks.put(newTask.getTaskID(), newTask);
        sortedTasksToTime.remove(oldTask);
        sortedTasksToTime.add(newTask);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        if (epics.get(epic.getTaskID()) != null) {
            Epic newEpic = new Epic(epic);
            epic.setName(newEpic.getName());
            epic.setDescription(newEpic.getDescription());
        }
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        if (subTask == null) {
            return;
        }
        Epic epic = epics.get(subTask.getEpicTaskID());
        if (epic == null) {
            return;
        }
        SubTask oldSubTask = subTasks.get(subTask.getTaskID());
        if (oldSubTask == null) {
            return;
        }
        SubTask newSubTask = new SubTask(subTask);
        if (checkTheCrossingTimeTask(newSubTask)) {
            return;
        }
        subTasks.put(newSubTask.getTaskID(), newSubTask);
        sortedTasksToTime.remove(oldSubTask);
        sortedTasksToTime.add(newSubTask);
        checkAndReplaceTaskProgress(epic);
        checkAndReplaceEpicTaskTime(epic);
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return;
        }
        tasks.remove(id);
        sortedTasksToTime.remove(task);
        history.remove(id);
    }

    @Override
    public void deleteEpicTask(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        epic.getSubTasksID().forEach(idSubTask -> {
            subTasks.remove(idSubTask);
            sortedTasksToTime.remove(subTasks.get(id));
            history.remove(idSubTask);
        });

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
            checkAndReplaceEpicTaskTime(epicToReplace);
        }
        subTasks.remove(id);
        sortedTasksToTime.remove(subTask);
        history.remove(id);
    }

    @Override
    public void deleteTasks() {
        tasks.values().forEach(task -> {
            history.remove(task.getTaskID());
            sortedTasksToTime.remove(task);
        });
        tasks.clear();
    }

    @Override
    public void deleteEpicTasks() {
        epics.values().stream().mapToInt(Task::getTaskID).forEach(history::remove);
        epics.clear();
        for (SubTask subTask : subTasks.values()) {
            history.remove(subTask.getTaskID());
            sortedTasksToTime.remove(subTask);
        }
        subTasks.clear();

    }

    @Override
    public void deleteSubTasks() {
        subTasks.values().forEach(subTask -> {
            history.remove(subTask.getTaskID());
            sortedTasksToTime.remove(subTask);
        });
        subTasks.clear();
        epics.values().forEach(epic -> {
            epic.removeAllSubTasks();
            checkAndReplaceTaskProgress(epic);
            checkAndReplaceEpicTaskTime(epic);
        });
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
            subTasksFromEpic = epic.getSubTasksID().stream()
                    .mapToInt(idSubTask -> idSubTask)
                    .mapToObj(subTasks::get)
                    .collect(Collectors.toList());
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
        boolean isInProgress = epic.getSubTasksID().stream()
                .map(id -> subTasks.get(id).getTaskProgress())
                .anyMatch(taskProgress -> taskProgress == TaskProgress.IN_PROGRESS
                        || taskProgress == TaskProgress.DONE);
        boolean isDone = epic.getSubTasksID().stream()
                .map(id -> subTasks.get(id).getTaskProgress())
                .allMatch(taskProgress -> taskProgress == TaskProgress.DONE);
        if (isDone) {
            epic.setTaskProgress(TaskProgress.DONE);
        } else if (isInProgress) {
            epic.setTaskProgress(TaskProgress.IN_PROGRESS);

        } else {
            epic.setTaskProgress(TaskProgress.NEW);
        }

    }

    protected void checkAndReplaceEpicTaskTime(Epic epic) {
        if (epic == null) {
            return;
        }
        List<SubTask> subTaskList = epic.getSubTasksID().stream()
                .map(subTasks::get)
                .filter(Objects::nonNull)
                .toList();
        if (!subTaskList.isEmpty()) {
            LocalDateTime startTime;
            Duration duration;
            LocalDateTime endTime;
            startTime = subTaskList.stream()
                    .map(SubTask::getStartTime)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
            duration = subTaskList.stream()
                    .map(SubTask::getDuration)
                    .filter(Objects::nonNull)
                    .reduce(Duration::plus)
                    .orElse(null);
            endTime = subTaskList.stream()
                    .map(SubTask::getEndTime)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            epic.updateTimesEpic(startTime, duration, endTime);
        }
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTasksToTime);
    }

    protected boolean checkTheCrossingTimeTask(Task task) {
        if (task == null
                || task.getType() == TaskType.EPIC
                || task.getStartTime() == null) {
            return false;
        }
        return sortedTasksToTime.stream()
                .filter(taskFromTreeSet -> taskFromTreeSet.getTaskID() != task.getTaskID())
                .anyMatch(taskFromTreeSet ->
                        taskFromTreeSet.getStartTime().isBefore(task.getEndTime())
                                && taskFromTreeSet.getEndTime().isAfter(task.getStartTime())

                );
    }
}
