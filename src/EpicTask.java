import java.util.ArrayList;
public class EpicTask extends Task {
    private final ArrayList<SubTask> subTasks;


    @Override
    public String toString() {
        return "EpicTask{" + super.toString() + "subTasks=" + subTasks + "} ";
    }

    public EpicTask(String name, String description, TaskProgress taskProgress, int taskID, ArrayList<String[]> formsToSubTask) {
        super(name, description, taskProgress, taskID);
        subTasks = new ArrayList<>();
        // Subtasks Создаются методом greatSubTask()
        greatSubTask(formsToSubTask);
    }

    private void greatSubTask(ArrayList<String[]> formsToSubTask) {
        if (!formsToSubTask.isEmpty()) {
            for (String[] form : formsToSubTask) {
                for (int i = 0; i < form.length; i++) {
                     /*Решил что подзадачи могут быть повторяющимися даже у одной задачи.
                     Например: Задача Приготовления еды.
                     Соль добавить на разных этапах готовки можно несколько раз*/
                    subTasks.add(new SubTask(form[0], form[1], TaskProgress.NEW, TaskManager.countTasks));

                }
            }
        }
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }
}
