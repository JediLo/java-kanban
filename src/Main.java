import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskProgress;

public class Main {
    public static TaskManager taskManager = new TaskManager();
    public static int idLastTaskAdd;
    public static int idLastEpicAdd;
    public static int idLastSubTaskAdd;

    public static void main(String[] args) {
        testAddAllTasks();
        testRemoveTask();
        testCheckAndReplaceTaskProgress();
        testUpdateTasks();
    }

    private static void testUpdateTasks() {
        addTasksToTest();
        System.out.println("Печать до обновления");
        printAllTask();
        //Создаем Task
        Task task = new Task("Обновленная задача Task", "Обновленное описание задачи Task");
        // Обновляем Task вручную, т.к. нужен объект не из Map в TaskManager
        task.setTaskID(idLastTaskAdd);
        task.setTaskProgress(TaskProgress.IN_PROGRESS);
        taskManager.updateTask(task);
        // Создаем  Epic
        Epic epic = new Epic("Обновленная задача Epic", "Обновленное описание задачи Epic");
        // Обновляем Epic вручную, т.к. нужен объект не из Map в TaskManager
        epic.setTaskID(idLastEpicAdd);
        epic.setTaskProgress(TaskProgress.NEW);
        taskManager.updateEpic(epic);
        // Создаем  SubTask вручную, т.к. нужен объект не из Map в TaskManager
        SubTask subTask = new SubTask("Обновленная задача SubTask",
                "Обновленное описание задачи SubTask", idLastEpicAdd);
        // Обновляем SubTask
        subTask.setTaskID(idLastSubTaskAdd);
        taskManager.updateSubtask(subTask);
        subTask.setTaskProgress(TaskProgress.DONE);
        System.out.println("Печать после обновления");
        printAllTask();

    }

    private static void testCheckAndReplaceTaskProgress() {
        System.out.println("Проверка обновления статуса");
        // Создаем и добавляем Epic
        Epic epic = new Epic("Задача Epic", "Описание задачи Epic");
        idLastEpicAdd = taskManager.addNewEpicTask(epic);
        taskManager.getEpicTask(idLastEpicAdd);
        // Создаем и добавляем и связываем SubTask
        SubTask subTask = new SubTask("Задача SubTask", "Описание задачи SubTask", idLastEpicAdd);
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask);
        taskManager.getSubTask(idLastSubTaskAdd);

        // Создаем и добавляем и связываем SubTask
        SubTask subTask1 = new SubTask("Задача SubTask", "Описание задачи SubTask", idLastEpicAdd);
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask1);
        taskManager.getSubTask(idLastSubTaskAdd);

        // Печатаем
        System.out.println("Печать до");
        printAllTask();
        // Меняем статус у 1го SubTask
        subTask.setTaskProgress(TaskProgress.IN_PROGRESS);
        taskManager.updateSubtask(subTask);
        // Запускаем метод проверки и изменения после чего печатаем

        System.out.println("Печать после");
        printAllTask();
        // Меняем у SubTask Статус обратно на NEW
        subTask.setTaskProgress(TaskProgress.NEW);
        taskManager.updateSubtask(subTask);
        // Запускаем метод проверки и изменения после чего печатаем

        System.out.println("Печать после");
        printAllTask();
        // Меняем все SubTask на DONE
        subTask.setTaskProgress(TaskProgress.DONE);
        subTask1.setTaskProgress(TaskProgress.DONE);
        taskManager.updateSubtask(subTask);
        taskManager.updateSubtask(subTask1);
        // Запускаем метод проверки и изменения после чего печатаем

        System.out.println("Печать после");
        printAllTask();
    }


    public static void printAllTask() {
        System.out.println("-".repeat(100));
        System.out.println("Печатаем все задачи");
        System.out.println("Все Task");
        for (Task taskToPrint : taskManager.getAllTask()) {
            System.out.println(taskToPrint);
        }
        System.out.println("Все Epic");
        for (Epic taskToPrint : taskManager.getAllEpic()) {
            System.out.println(taskToPrint);
        }
        System.out.println("Все SubTask");
        for (SubTask taskToPrint : taskManager.getAllSubTask()) {
            System.out.println(taskToPrint);
        }
        System.out.println("-".repeat(100));
    }

    public static void addTasksToTest() {
        // Создаем и добавляем Task
        Task task = new Task("Задача Task", "Описание задачи Task");
        idLastTaskAdd = taskManager.addNewTask(task);
        // Создаем и добавляем Epic
        Epic epic = new Epic("Задача Epic", "Описание задачи Epic");
        idLastEpicAdd = taskManager.addNewEpicTask(epic);
        // Создаем и добавляем SubTask
        SubTask subTask = new SubTask("Задача SubTask", "Описание задачи SubTask", idLastEpicAdd);
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask);
        // Создаем и добавляем SubTask
        subTask = new SubTask("Задача SubTask 1", "Описание задачи SubTask 1", idLastEpicAdd);
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask);
        // Создаем и добавляем SubTask
        subTask = new SubTask("Задача SubTask 2", "Описание задачи SubTask 2", idLastEpicAdd);
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask);
    }

    public static void testAddAllTasks() {
        System.out.println("Проверка на добавление Задач");
        addTasksToTest();
        addTasksToTest();
        printAllTask();
    }

    public static void testRemoveTask() {
        addTasksToTest();
        addTasksToTest();
        printAllTask();
        System.out.println("Проверка на удаление задач");
        // idTas
        taskManager.deleteTask(idLastTaskAdd);
        taskManager.deleteEpicTask(idLastEpicAdd);
        taskManager.deleteSubtask(3);


        printAllTask();

        System.out.println("Проверка на удаление всех Tasks всех Epic и всех Sub");
        taskManager.deleteTasks();
        taskManager.deleteSubTasks();
        taskManager.deleteEpicTasks();
        printAllTask();
    }


}
