package ru.practicum.model;

public class SubTask extends Task {
    public int getEpicTaskID() {
        return EpicTaskID;
    }

    private int EpicTaskID;

    public SubTask(String name , String description) {
        super(name,  description);

    }

    @Override
    public String toString() {
        return "SubTask{" + super.toString() +
                "EpicTaskID=" + EpicTaskID +
                "} "  ;
    }

    public void setEpicTaskID(int epicTaskID) {
        EpicTaskID = epicTaskID;
    }
}
