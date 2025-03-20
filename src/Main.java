import ru.practicum.manager.TaskManager;
import ru.practicum.model.EpicTask;
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
        testAddSubTaskToEpic();
        testRemoveSubTaskFromEpic();
        testUpdateTasks();
        testCheckAndReplaceTaskProgress();
    }

    private static void testCheckAndReplaceTaskProgress() {
        System.out.println("Проверка обновления статуса");
        // Создаем и добавляем EpicTask
        EpicTask epicTask = new EpicTask("Задача EpicTask", "Описание задачи EpicTask");
        idLastEpicAdd = taskManager.addNewEpicTask(epicTask);
        taskManager.getEpicTask(idLastEpicAdd);
        // Создаем и добавляем и связываем SubTask
        SubTask subTask = new SubTask("Задача SubTask", "Описание задачи SubTask");
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask);
        taskManager.getSubTask(idLastSubTaskAdd);
        epicTask.addSubTaskToEpic(subTask);
        // Создаем и добавляем и связываем SubTask
        SubTask subTask1 = new SubTask("Задача SubTask", "Описание задачи SubTask");
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask1);
        taskManager.getSubTask(idLastSubTaskAdd);
        epicTask.addSubTaskToEpic(subTask1);
        // Печатаем
        System.out.println("Печать до");
        printAllTask();
        // Меняем статус у 1го SubTask
        subTask.setTaskProgress(TaskProgress.IN_PROGRESS);
        // Запускаем метод проверки и изменения после чего печатаем
        taskManager.checkAndReplaceTaskProgress(epicTask);
        System.out.println("Печать после");
        printAllTask();
        // Меняем у SubTask Статус обратно на NEW
        subTask.setTaskProgress(TaskProgress.NEW);
        // Запускаем метод проверки и изменения после чего печатаем
        taskManager.checkAndReplaceTaskProgress(epicTask);
        System.out.println("Печать после");
        printAllTask();
        // Меняем все SubTask на DONE
        subTask.setTaskProgress(TaskProgress.DONE);
        subTask1.setTaskProgress(TaskProgress.DONE);
        // Запускаем метод проверки и изменения после чего печатаем
        taskManager.checkAndReplaceTaskProgress(epicTask);
        System.out.println("Печать после");
        printAllTask();
    }


    private static void testUpdateTasks() {
        System.out.println("Проверка на обновление Задач");

        // Создаем и добавляем Task
        Task task = new Task("Задача Task", "Описание задачи Task");
        idLastTaskAdd = taskManager.addNewTask(task);
        taskManager.getTask(idLastTaskAdd);
        // Создаем и добавляем EpicTask
        EpicTask epicTask = new EpicTask("Задача EpicTask", "Описание задачи EpicTask");
        idLastEpicAdd = taskManager.addNewEpicTask(epicTask);
        taskManager.getEpicTask(idLastEpicAdd);
        // Создаем и добавляем SubTask
        SubTask subTask = new SubTask("Задача SubTask", "Описание задачи SubTask");
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask);
        taskManager.getSubTask(idLastSubTaskAdd);

        // Создадим и отредактируем в ручную

        // Создаем и редактируем Task
        task = new Task("Измененная задача Task", "Измененное описание задачи Task");
        task.setTaskProgress(TaskProgress.IN_PROGRESS);
        task.setTaskID(idLastTaskAdd);
        // Создаем и редактируем EpicTask
        epicTask = new EpicTask("Измененная задача EpicTask", "Измененное описание задачи EpicTask");
        epicTask.setTaskProgress(TaskProgress.IN_PROGRESS);
        epicTask.setTaskID(idLastEpicAdd);
        // Создаем и редактируем SubTask
        subTask = new SubTask("Измененная задача SubTask", "Измененное описание задачи SubTask");
        subTask.setTaskProgress(TaskProgress.DONE);
        subTask.setTaskID(idLastSubTaskAdd);
        // Печать до обновления
        printAllTask();
        // Обновляем
        taskManager.updateTask(task);
        taskManager.updateEpic(epicTask);
        taskManager.updateSubtask(subTask);
        // Печать после обновления
        printAllTask();
    }

    private static void testRemoveSubTaskFromEpic() {
        System.out.println("Проверка отвязки SubTask От Epic");
        // Создаем и добавляем EpicTask
        EpicTask epicTask = new EpicTask("Задача EpicTask", "Описание задачи EpicTask");
        idLastEpicAdd = taskManager.addNewEpicTask(epicTask);
        taskManager.getEpicTask(idLastEpicAdd);
        // Создаем и добавляем и связываем SubTask
        SubTask subTask = new SubTask("Задача SubTask", "Описание задачи SubTask");
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask);
        taskManager.getSubTask(idLastSubTaskAdd);
        epicTask.addSubTaskToEpic(subTask);
        // Создаем и добавляем и связываем SubTask
        subTask = new SubTask("Задача SubTask", "Описание задачи SubTask");
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask);
        taskManager.getSubTask(idLastSubTaskAdd);
        epicTask.addSubTaskToEpic(subTask);
        epicTask.addSubTaskToEpic(subTask);
        // Печатаем
        System.out.println("Печать до отвязки");
        printAllTask();
        //Отвязываем
        epicTask.removeSubTaskFromEpic(subTask);
        //Печатаем еще раз, чтобы убедиться, что метод работает.
        System.out.println("Печать после отвязки");
        printAllTask();
        // Привязываем вновь и теперь отвязываем сразу все
        epicTask.addSubTaskToEpic(subTask);
        printAllTask();
        epicTask.removeAllSubTasksFromEpic(taskManager.getAllSubTask());
        printAllTask();
    }

    public static void printAllTask() {
        System.out.println("-".repeat(100));
        System.out.println("Печатаем все задачи");
        System.out.println("Все Task");
        for (Task taskToPrint : taskManager.getAllTask()) {
            System.out.println(taskToPrint);
        }
        System.out.println("Все EpicTask");
        for (EpicTask taskToPrint : taskManager.getAllEpic()) {
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
        taskManager.getTask(idLastTaskAdd);
        // Создаем и добавляем EpicTask
        EpicTask epicTask = new EpicTask("Задача EpicTask", "Описание задачи EpicTask");
        idLastEpicAdd = taskManager.addNewEpicTask(epicTask);
        taskManager.getEpicTask(idLastEpicAdd);
        // Создаем и добавляем SubTask
        SubTask subTask = new SubTask("Задача SubTask", "Описание задачи SubTask");
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask);
        taskManager.getSubTask(idLastSubTaskAdd);
        // Создаем и добавляем SubTask
        subTask = new SubTask("Задача SubTask 1", "Описание задачи SubTask 1");
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask);
        taskManager.getSubTask(idLastSubTaskAdd);
        // Создаем и добавляем SubTask
        subTask = new SubTask("Задача SubTask 2", "Описание задачи SubTask 2");
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask);
        taskManager.getSubTask(idLastSubTaskAdd);
    }

    public static void testAddAllTasks() {
        System.out.println("Проверка на добавление Задач");
        addTasksToTest();
        addTasksToTest();
        printAllTask();
    }

    public static void testRemoveTask() {
        addTasksToTest();
        System.out.println("Проверка на удаление задач");
        // idTas
        taskManager.deleteTask(idLastTaskAdd);
        taskManager.deleteEpicTask(idLastEpicAdd);
        taskManager.deleteSubtask(idLastSubTaskAdd);
        printAllTask();

        System.out.println("Проверка на удаление всех Tasks всех Epic и всех Sub");
        taskManager.deleteTasks();
        taskManager.deleteSubTasks();
        taskManager.deleteEpicTasks();
        printAllTask();
    }

    public static void testAddSubTaskToEpic() {
        System.out.println("Проверка на добавление subTask в Epic");

        // Создаем и добавляем EpicTask
        EpicTask epicTask = new EpicTask("Задача EpicTask", "Описание задачи EpicTask");
        idLastEpicAdd = taskManager.addNewEpicTask(epicTask);
        taskManager.getEpicTask(idLastEpicAdd);
        // Создаем и добавляем SubTask
        SubTask subTaskForAdd =
                new SubTask("Задача subTaskForRemove", "Описание задачи subTaskForRemove");
        taskManager.addNewSubTask(subTaskForAdd);
        // Создаем и добавляем SubTask
        SubTask subTaskForAdd1 =
                new SubTask("Задача subTaskForRemove", "Описание задачи subTaskForRemove");
        taskManager.addNewSubTask(subTaskForAdd1);
        // Создаем и добавляем SubTask
        SubTask subTaskForAdd2 =
                new SubTask("Задача subTaskForRemove", "Описание задачи subTaskForRemove");
        taskManager.addNewSubTask(subTaskForAdd2);
        // Создаем и добавляем SubTask
        SubTask subTaskForAdd3 =
                new SubTask("Задача subTaskForRemove", "Описание задачи subTaskForRemove");
        taskManager.addNewSubTask(subTaskForAdd3);
        System.out.println("До привязки");
        printAllTask();
        epicTask.addSubTaskToEpic(subTaskForAdd);
        epicTask.addSubTaskToEpic(subTaskForAdd1);
        epicTask.addSubTaskToEpic(subTaskForAdd2);
        epicTask.addSubTaskToEpic(subTaskForAdd3);
        System.out.println("После привязки");
        printAllTask();
    }
}
