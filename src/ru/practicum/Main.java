package ru.practicum;

import ru.practicum.manager.general.Managers;
import ru.practicum.manager.general.TaskManager;
import ru.practicum.model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    private static final TaskManager taskManager = Managers.getDefault();
    private static final LocalDateTime timeToFirstTask = LocalDateTime.now();
    private static final LocalDateTime timeToSecondTask = timeToFirstTask.plusMinutes(2);
    private static final LocalDateTime timeToThirdTask = timeToSecondTask.plusMinutes(2);
    private static final LocalDateTime timeToFourTask = timeToThirdTask.plusMinutes(2);
    private static final Duration duration = Duration.ofMinutes(1);
    private static int idLastTaskAdd;
    private static int idLastEpicAdd;
    private static int idLastSubTaskAdd;
    private static int countID = 0;

    public static void main(String[] args) {

        addTasksToTest();
        //printAllTask();
        //System.out.println();
        //addTasksToTest();
    }

    private static void testFileBackedManager() {
        Task task = new Task(1, TaskType.TASK, "Name",
                TaskProgress.NEW, "description", timeToFirstTask, duration);
        taskManager.addNewTask(task);

    }

    private static void testReturnTasks() {
        System.out.println("Проверяем методы возращения");
        System.out.println(taskManager.getTask(idLastTaskAdd));
        System.out.println(taskManager.getSubTask(idLastSubTaskAdd));
        System.out.println(taskManager.getEpicTask(idLastEpicAdd));
        System.out.println(taskManager.getSubTasksFromEpic(idLastEpicAdd));
        System.out.println("Получаем сразу списки всех Задач");
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllSubTask());


    }


    public static void printAllTask() {
        System.out.println("-".repeat(100));
        System.out.println("Печатаем все задачи");
        for (Task task : taskManager.getAllTask()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : taskManager.getAllEpic()) {
            System.out.println(epic);

            for (Task task : taskManager.getSubTasksFromEpic(epic.getTaskID())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.getAllSubTask()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-".repeat(100));
        System.out.println("-".repeat(100));
        System.out.println("-".repeat(100));
    }

    public static void addTasksToTest() {
        // Создаем и добавляем Task
        countID++;
        Task task = new Task(countID, TaskType.TASK, "Задача Task",
                TaskProgress.NEW, "Описание задачи Task", timeToFirstTask, duration);
        idLastTaskAdd = taskManager.addNewTask(task);
        // Создаем и добавляем Epic
        countID++;
        Epic epic = new Epic(countID, TaskType.EPIC, "Задача Epic",
                TaskProgress.NEW, "Описание задачи Epic");
        idLastEpicAdd = taskManager.addNewEpicTask(epic);
        // Создаем и добавляем пустой Epic
        countID++;
        Epic secondEpic = new Epic(countID, TaskType.EPIC, "Задача Epic",
                TaskProgress.NEW, "Описание задачи Epic");
        taskManager.addNewEpicTask(secondEpic);
        // Создаем и добавляем SubTask
        countID++;
        SubTask subTask = new SubTask(countID, TaskType.SUBTASK, "Задача SubTask",
                TaskProgress.NEW, "Описание задачи SubTask",
                idLastEpicAdd, timeToSecondTask, duration);
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask);
        // Создаем и добавляем SubTask
        countID++;
        subTask = new SubTask(countID, TaskType.SUBTASK, "Задача SubTask 1",
                TaskProgress.NEW, "Описание задачи SubTask 1", idLastEpicAdd, timeToThirdTask, duration);
        idLastSubTaskAdd = taskManager.addNewSubTask(subTask);
        // Создаем и добавляем SubTask
        countID++;
        subTask = new SubTask(countID, TaskType.SUBTASK, "Задача SubTask 2",
                TaskProgress.NEW, "Описание задачи SubTask 2",
                idLastEpicAdd, timeToFourTask, duration);
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
