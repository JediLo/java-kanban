package ru.practicum.model;

import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int taskID;
    protected TaskProgress taskProgress;
    protected TaskType taskType;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public Task(int id, TaskType taskType, String name, TaskProgress taskProgress, String description) {
        this.taskID = id;
        this.taskType = taskType;
        this.name = name;
        this.taskProgress = taskProgress;
        this.description = description;
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
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public void setTaskProgress(TaskProgress taskProgress) {
        this.taskProgress = taskProgress;
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

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskID=" + taskID +
                ", taskProgress=" + taskProgress +
                ", taskType=" + taskType +
                '}';
    }

    public TaskProgress getTaskProgress() {
        return taskProgress;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
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


