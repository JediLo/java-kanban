package ru.practicum.model;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int taskID;
    private TaskProgress taskProgress;


    public Task(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public void setTaskProgress(TaskProgress taskProgress) {
        this.taskProgress = taskProgress;
    }

    /*В equals и hashCode оставил только проверку по ID потому как остальные поля могут отличаться
     в переданном объекте в методы: updateTask, updateEpic, updateSubtask*/
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
}


