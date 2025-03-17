public class Task {
    private String name;
    private String description;
    private final int taskID;
    private TaskProgress taskProgress;

    public Task(String name, String description, TaskProgress taskProgress, int taskID) {
        this.name = name;
        this.description = description;
        this.taskProgress = taskProgress;
        this.taskID = taskID;
        TaskManager.countTasks++;
    }

    @Override
    public String toString() {
        return "Task{" +
                "Название задачи='" + name + '\'' +
                ", Описание задачи='" + description + '\'' +
                ", Идентификационный номер=" + taskID +
                ", Статус=" + taskProgress +
                '}';
    }

    public int getTaskID() {
        return taskID;
    }


    public void setTaskProgress(TaskProgress taskProgress) {
        this.taskProgress = taskProgress;
    }

    public TaskProgress getTaskProgress() {
        return taskProgress;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }
}
