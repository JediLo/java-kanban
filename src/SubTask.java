public class SubTask extends Task {
    public SubTask(String name, String description, TaskProgress taskProgress, int taskID) {
        super(name, description, taskProgress, taskID);
    }


    @Override
    public String toString() {
        return "SubTask{ " + super.toString() +
                "} ";
    }
}
