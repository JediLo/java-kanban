package ru.practicum.model;

public class SubTask extends Task {
    private int epicTaskID;

    public SubTask(String name, String description, int epicTaskID) {
        super(name, description);
        this.epicTaskID = epicTaskID;
    }

    public SubTask(SubTask subTask) {
        super(subTask);
        this.epicTaskID = subTask.getEpicTaskID();
    }

    public SubTask(int id, TaskType taskType, String name, TaskProgress taskProgress, String description, int epicTaskID) {
        super(id, taskType, name, taskProgress, description);
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

    public void setEpicTaskID(int epicTaskID) {
        this.epicTaskID = epicTaskID;
    }
}
