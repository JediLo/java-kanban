package ru.practicum.model;

import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    public List<Integer> getSubTasksID() {
        return subTasksID;
    }

    private final List<Integer> subTasksID = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);

    }

    public void addSubTask(SubTask subTask) {
        this.subTasksID.add(subTask.getTaskID());

    }

    public void removeSubTask(SubTask subTask) {
        this.subTasksID.remove((Integer) subTask.getTaskID());
    }

    public void removeAllSubTasks() {
        subTasksID.clear();
    }


    @Override
    public String toString() {
        return "Epic{" + super.toString() +
                "subTasksID=" + subTasksID +
                "} ";
    }
}
