package ru.practicum.model;

public class SubTask extends Task {
    private final int epicTaskID;

    public SubTask(String name, String description, int epicTaskID) {
        super(name, description);
        this.epicTaskID = epicTaskID;
    }

    public int getEpicTaskID() {
        return epicTaskID;
    }

    @Override
    public String toString() {
        return "SubTask{" + super.toString() +
                "EpicTaskID=" + epicTaskID +
                "} ";
    }

}
