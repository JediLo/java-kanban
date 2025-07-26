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
    protected int countTasks = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();
    protected final TreeSet<Task> sortedTasksToTime = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        history.add(task);
        return tasks.get(id);
    }

    @Override
    public Epic getEpicTask(int id) {
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
        if (checkTheCrossingTimeTask(task)) {
            return -1;
        }
        countTasks++;
        task.setTaskID(countTasks);
        task.setTaskProgress(TaskProgress.NEW);
        task.setTaskType(TaskType.TASK);
        tasks.put(countTasks, task);
        sortedTasksToTime.add(task);
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
        if (checkTheCrossingTimeTask(subTask)) {
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
            sortedTasksToTime.add(subTask);
            epic.addSubTask(subTask);
            checkAndReplaceTaskProgress(epic);
            checkAndReplaceEpicTaskTime(epic);
            return countTasks;
        }

    }

    @Override
    public void updateTask(Task newTask) {
        if (newTask == null) {
            return;
        }
        Task task = tasks.get(newTask.getTaskID());
        if (task == null) {
            return;
        }
        sortedTasksToTime.remove(task);
        if (checkTheCrossingTimeTask(newTask)) {
            sortedTasksToTime.add(task);
            return;
        }
        tasks.put(newTask.getTaskID(), newTask);
        sortedTasksToTime.add(newTask);
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
        if (subTask == null) {
            return;
        }

        sortedTasksToTime.remove(subTask);
        if (checkTheCrossingTimeTask(newSubTask)) {
            sortedTasksToTime.add(subTask);
            return;
        }
        subTasks.put(newSubTask.getTaskID(), newSubTask);
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

    private void checkAndReplaceEpicTaskTime(Epic epic) {
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

    public TreeSet<Task> getPrioritizedTasks() {
        return sortedTasksToTime;
    }

    private boolean checkTheCrossingTimeTask(Task task) {
        if (task == null
                || task.getType() == TaskType.EPIC
                || task.getStartTime() == null) {
            return false;
        }
        return sortedTasksToTime.stream().anyMatch(taskFromTreeSet ->
                taskFromTreeSet.getStartTime().isBefore(task.getEndTime())
                        && taskFromTreeSet.getEndTime().isAfter(task.getStartTime())

        );
    }
}
