import java.util.ArrayList;

public class TaskManager {
    public static int countTasks = 1;
    public ArrayList<Object> tasks;

    public TaskManager() {
        this.tasks = new ArrayList<>();
    }


    public void addTask(String[] form) {
        if (checkCloneTask(form)) {
            tasks.add(new Task(form[0], form[1], TaskProgress.NEW, countTasks));
        }

    }

    public void addTask(String[] form, ArrayList<String[]> formToSubTasks) {
        tasks.add(new EpicTask(form[0], form[1], TaskProgress.NEW, countTasks, formToSubTasks));

    }

    private boolean checkCloneTask(String[] form) {
        //Проверяет не пытаются ли добавить Задачу в которой Название и описание идентичные. (Возвращает true если поля идентичные)
        for (Object object : tasks) {
            Task task = ((Task) object);
            if (task.getName().equals(form[0]) &&
                    task.getDescription().equals(form[1])) {
                return false;
            }
        }
        return true;
    }

    public void printTasks() {
        for (Object task : tasks) {
            System.out.println(task);
        }
    }

    public void printTasksWithSubTask() {
        // Если использовать этот метод, то в методе toString из класса EpicTask нужно убрать печать SubTask
        for (Object task : tasks) {
            System.out.println(task);
            if (task.getClass() == EpicTask.class) {
                for (SubTask subTask : ((EpicTask) task).getSubTasks()) {
                    System.out.println(subTask);

                }
            }
        }
    }

    public Object findByID(int id) {

        if (tasks.isEmpty()) {
            return null;
        } else {
            for (Object object : tasks) {
                Object obj = findByIDinObject(object, id);
                if (obj != null) return obj;
            }
        }
        return null;
    }

    private Object findByIDinObject(Object o, int id) {
        // Метод проверки Задачи на соответствие ID В переданном Task, так же ищет SubTask вложенный в EpicTask
        Task task = (Task) o;
        if (task.getTaskID() == id) {
            return task;
        }
        if (task.getClass() == EpicTask.class) {
            for (SubTask subTask : ((EpicTask) task).getSubTasks()) {
                if (subTask.getTaskID() == id) {
                    return subTask;

                }
            }
        }
        return null;
    }

    public void editTask(int id, TaskProgress taskProgress) {
        if (tasks.isEmpty()) {
            System.out.println("Еще нет задач");
        } else {
            for (Object object : tasks) {
                Object obj = findByIDinObject(object, id);
                if (obj != null) {
                    ((Task) obj).setTaskProgress(taskProgress);
                    if (obj.getClass() == SubTask.class) {
                        editEpicTask(object);
                    }

                }
            }
        }
    }
    public void editTask(int id, String[] newForm){
        if(tasks.isEmpty()){
            System.out.println("Еще нет задач");
        }else {
            for (Object object : tasks){
                Object obj = findByIDinObject(object, id);
                if (obj != null &&
                        newForm != null &&
                        checkCloneTask(newForm)){
                    ((Task) obj).setName(newForm[0]);
                    ((Task) obj).setDescription(newForm[1]);
                }
            }
        }
    }
    private void editEpicTask(Object epicTask) {
    /*Метод проверяет остальные связанный SubTask и если все DONE тогда меняет и EPIC на DONE.
    По той же логике если хоть 1 Sub IN_PROGRESS или DONE меняет Epic на IN_PROGRESS
    Обработки на статус NEW не делал, т.к. если задачу уже редактировали, значит она не новая*/
        ArrayList<SubTask> subTasks = ((EpicTask) epicTask).getSubTasks();
        TaskProgress taskProgressToSubTask;
        for (int i = 0; i < subTasks.size(); i++) {
            taskProgressToSubTask = subTasks.get(i).getTaskProgress();
            if (taskProgressToSubTask == TaskProgress.IN_PROGRESS ||
                    taskProgressToSubTask == TaskProgress.DONE) {
                ((EpicTask) epicTask).setTaskProgress(TaskProgress.IN_PROGRESS);
            }
            if (taskProgressToSubTask != TaskProgress.DONE) {
                return;
            } else if (i == (subTasks.size() - 1)) {
                ((EpicTask) epicTask).setTaskProgress(TaskProgress.DONE);
            }
        }
    }

    public void removeByID(int id) {
        if (tasks.isEmpty()) {
            System.out.println("Еще нет задач");
        } else {
            for (Object object : tasks) {

                Object obj = findByIDinObject(object, id);
                if (obj != null) {
                    if (obj.getClass() != SubTask.class) {
                        tasks.remove(obj);
                    } else {
                        ((EpicTask) object).getSubTasks().remove(obj);
                    }
                }
            }
        }
    }

    public void removeAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
            countTasks = 1;
        }
    }
}
