package ru.practicum.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Task {
    protected int taskID;
    protected TaskType taskType;
    protected String name;
    protected TaskProgress taskProgress;
    protected String description;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(int taskID, TaskType taskType, String name, TaskProgress taskProgress, String description, LocalDateTime startTime, Duration duration) {
        this.taskID = taskID;
        this.taskType = taskType;
        this.name = name;
        this.taskProgress = taskProgress;
        this.description = description;
        if (startTime != null) {
            this.startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
        }
        this.duration = duration;
    }


    public Task(Task task) {
        if (task == null) {
            return;
        }
        this.name = task.getName();
        this.description = task.getDescription();
        this.taskID = task.taskID;
        this.taskProgress = task.taskProgress;
        this.taskType = task.taskType;
        this.duration = task.duration;
        if (task.startTime != null) {
            this.startTime = task.startTime.truncatedTo(ChronoUnit.MINUTES);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskID == task.taskID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskID);
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskID=" + taskID +
                ", taskProgress=" + taskProgress +
                ", taskType=" + taskType +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                '}';
    }

    public TaskProgress getTaskProgress() {
        return taskProgress;
    }

    public void setTaskProgress(TaskProgress taskProgress) {
        this.taskProgress = taskProgress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskType getType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }
}


