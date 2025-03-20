package ru.practicum.model;

import ru.practicum.manager.TaskManager;


import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private final List<Integer> subTasksID = new ArrayList<>();

    public EpicTask(String name, String description) {
        super(name , description);

    }

    public void addSubTaskToEpic(SubTask subTask){
        if(this.getTaskID() != subTask.getEpicTaskID()) {
            this.subTasksID.add(subTask.getTaskID());
            subTask.setEpicTaskID(this.getTaskID());

    }

    }
    public void removeSubTaskFromEpic(SubTask subTask){

            this.subTasksID.remove((Integer) subTask.getTaskID());
            subTask.setEpicTaskID(DEFAULT_ID_TASK);

    }
    public void removeAllSubTasksFromEpic(){

        subTasksID.clear();
    }


    @Override
    public String toString() {
        return "EpicTask{" + super.toString()+
                "subTasksID=" + subTasksID +
                "} " ;
    }
}
