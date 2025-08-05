package ru.practicum.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicTaskID;

    public SubTask(SubTask subTask) {
        super(subTask);
        this.epicTaskID = subTask.getEpicTaskID();
    }

    public SubTask(int taskID, TaskType taskType, String name, TaskProgress taskProgress,
                   String description, int epicTaskID, LocalDateTime startTime, Duration duration) {
        super(taskID, taskType, name, taskProgress, description, startTime, duration);
        this.epicTaskID = epicTaskID;
    }

    public int getEpicTaskID() {
        return epicTaskID;
    }

    public void setEpicTaskID(int epicTaskID) {
        this.epicTaskID = epicTaskID;
    }

    @Override
    public String toString() {
        return "SubTask{" + super.toString() +
                "EpicTaskID=" + epicTaskID +
                "} ";
    }
}
