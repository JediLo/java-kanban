package ru.practicum.manager.file;

import ru.practicum.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManagerSCV {
    protected static final String title = "id,type,name,status,description,epic,start,duration";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm_dd_MM_yyyy");

    public static String getSCVFromTasks(List<Task> tasks) {
        if (tasks == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        Task taskToCSV;
        for (Task task : tasks) {
            String epicId = " ";
            if (task instanceof SubTask subTask) {
                taskToCSV = new SubTask(subTask);
                epicId = String.valueOf(((SubTask) taskToCSV).getEpicTaskID());
            } else if (task instanceof Epic epic) {
                taskToCSV = new Epic(epic);

            } else {
                taskToCSV = new Task(task);
            }
            sb.append(System.lineSeparator()).append(taskToCSV.getTaskID()).append(",")
                    .append(taskToCSV.getType()).append(",")
                    .append(taskToCSV.getName()).append(",")
                    .append(taskToCSV.getTaskProgress()).append(",")
                    .append(taskToCSV.getDescription()).append(",")
                    .append(epicId).append(",");
            if (taskToCSV.getStartTime() != null) {
                sb.append(taskToCSV.getStartTime().format(formatter)).append(",");
            } else {
                sb.append(" ,");
            }
            if (taskToCSV.getDuration() != null) {
                sb.append(taskToCSV.getDuration().toNanos());
            } else {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static Task getTaskFromString(String line) {
        if (line == null || line.equals(title)) {
            return null;
        }
        String[] elements = line.split(",");
        int id = Integer.parseInt(elements[0]);
        TaskType taskType = TaskType.valueOf(elements[1]);
        String name = elements[2];
        TaskProgress taskProgress = TaskProgress.valueOf(elements[3]);
        String description = elements[4];
        LocalDateTime startTime = null;
        Duration duration = null;
        if (!elements[6].isBlank()) {
            startTime = LocalDateTime.parse(elements[6], formatter);
        }
        if (!elements[7].isBlank()) {
            duration = Duration.ofNanos(Long.parseLong(elements[7]));
        }
        return switch (taskType) {
            case TASK -> new Task(id, taskType, name, taskProgress, description, startTime, duration);
            case EPIC -> new Epic(id, taskType, name, taskProgress, description);
            case SUBTASK -> new SubTask(id, taskType, name, taskProgress, description, Integer.parseInt(elements[5]),
                    startTime, duration);
        };
    }
}
