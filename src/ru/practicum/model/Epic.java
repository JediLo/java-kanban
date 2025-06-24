package ru.practicum.model;

import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    private List<Integer> subTasksID = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);

    }
    public Epic(Epic epic){
        super(epic);
        this.subTasksID = epic.getSubTasksID();
    }
    public List<Integer> getSubTasksID() {
        return subTasksID;
    }
    public void addSubTask(SubTask subTask) {
        if (subTask == null){
            return;
        }
        this.subTasksID.add(subTask.getTaskID());

    }

    public void removeSubTask(SubTask subTask) {
        if (subTask == null){
            return;
        }
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
