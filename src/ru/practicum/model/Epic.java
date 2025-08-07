package ru.practicum.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    private List<Integer> subTasksID = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(Epic epic) {
        super(epic);
        this.subTasksID = epic.getSubTasksID();
        this.endTime = epic.endTime;
    }

    public Epic(String name, String description) {
        super(name, description,
                // Передаются null т.к. поля рассчитываются исходя из подзадач
                null,null);
    }

    public Epic(int id, TaskType taskType, String name, TaskProgress taskProgress,
                String description) {
        super(id, taskType, name, taskProgress, description,
                // Передаются null т.к. поля рассчитываются исходя из подзадач
                null, null);
    }

    public List<Integer> getSubTasksID() {
        return subTasksID;
    }

    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            return;
        }
        this.subTasksID.add(subTask.getTaskID());

    }

    public void updateTimesEpic(LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        if (startTime != null && endTime != null) {
            this.startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
            this.endTime = endTime.truncatedTo(ChronoUnit.MINUTES);
        }
        this.duration = duration;

    }

    public void removeSubTask(SubTask subTask) {
        if (subTask == null) {
            return;
        }
        this.subTasksID.remove((Integer) subTask.getTaskID());
    }

    public void removeAllSubTasks() {
        this.startTime = null;
        this.duration = null;
        this.endTime = null;
        subTasksID.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" + super.toString() +
                "subTasksID=" + subTasksID +
                "} ";
    }

}
