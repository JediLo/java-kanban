package ru.practicum.manager.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager manager;
    LocalDateTime timeToFirstTask = LocalDateTime.now();
    LocalDateTime timeToSecondTask = timeToFirstTask.plusMinutes(2);
    LocalDateTime timeToThirdTask = timeToSecondTask.plusMinutes(2);
    Duration duration = Duration.ofMinutes(1);

    @BeforeEach
    void setup() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void shouldAddTaskWithNewStatusAndCorrectId() {

        Task task = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToFirstTask, duration);
        int idTask = manager.addNewTask(task);
        assertEquals(TaskProgress.NEW, manager.getTask(idTask).getTaskProgress(),
                "Статус не был обновлен на NEW в Task");
        assertEquals(idTask, manager.getTask(idTask).getTaskID(), "Неверный ID в Task");
    }

    @Test
    void shouldAddEpicWithNewStatusAndCorrectId() {

        Epic epic = new Epic(1, TaskType.EPIC, "Name Epic", TaskProgress.NEW
                , "Description Epic");
        int idEpic = manager.addNewEpicTask(epic);
        assertEquals(TaskProgress.NEW, manager.getEpicTask(idEpic).getTaskProgress(),
                "Статус не был обновлен на NEW в Epic");
        assertEquals(idEpic, manager.getEpicTask(idEpic).getTaskID(), "Неверный ID в Epic");
    }

    @Test
    void shouldAddSubTaskWithNewStatusAndCorrectId() {

        Epic epic = new Epic(1, TaskType.EPIC, "Name Epic", TaskProgress.NEW, "Description Epic");
        int idEpic = manager.addNewEpicTask(epic);
        SubTask subTask = new SubTask(2, TaskType.SUBTASK, "Name Task"
                , TaskProgress.NEW, "Description Task", idEpic, timeToFirstTask, duration);
        int idSubTask = manager.addNewSubTask(subTask);

        assertEquals(TaskProgress.NEW, manager.getSubTask(idSubTask).getTaskProgress(),
                "Статус не был обновлен на NEW в SubTask");
        assertEquals(idSubTask, manager.getSubTask(idSubTask).getTaskID(), "Неверный ID в SubTask");
    }

    @Test
    void shouldNewNameAndDescriptionAndTaskProgressWhenUpdateTask() {
        int idTask = 1;
        Task task = new Task(idTask, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToFirstTask, duration);
        manager.addNewTask(task);
        Task updateTask = new Task(idTask, TaskType.TASK, "Name New Task"
                , TaskProgress.IN_PROGRESS, "Description New Task", timeToSecondTask, duration);
        manager.updateTask(updateTask);
        assertEquals("Name New Task", manager.getTask(idTask).getName(), "Name не обновлен");
        assertEquals("Description New Task",
                manager.getTask(idTask).getDescription(), "Description не обновлен");
        assertEquals(TaskProgress.IN_PROGRESS, manager.getTask(idTask).getTaskProgress(), "Статус не обновлен");
    }

    @Test
    void shouldNewNameAndDescriptionWhenUpdateEpic() {
        int idEpic = 1;
        Epic epic = new Epic(idEpic, TaskType.EPIC, "Name Epic"
                , TaskProgress.NEW, "Description Epic");
        manager.addNewEpicTask(epic);
        Epic updateEpic = new Epic(idEpic, TaskType.EPIC, "Name New Epic"
                , TaskProgress.NEW, "Description New Epic");
        manager.updateEpic(updateEpic);
        assertEquals("Name New Epic", manager.getEpicTask(idEpic).getName(), "Name не обновлен");
        assertEquals("Description New Epic",
                manager.getEpicTask(idEpic).getDescription(), "Description не обновлен");
    }

    @Test
    void shouldNewNameAndDescriptionAndTaskProgressWhenUpdateSubTask() {
        int idEpic = manager.addNewEpicTask(new Epic(1, TaskType.EPIC, "Name Epic", TaskProgress.NEW, "Description Epic"));
        SubTask updateSubTask = createSubTask(idEpic);
        int idSubTask = updateSubTask.getTaskID();
        updateSubTask.setTaskProgress(TaskProgress.IN_PROGRESS);
        manager.updateSubtask(updateSubTask);
        assertEquals("Name New SubTask", manager.getSubTask(idSubTask).getName(),
                "Name не обновлен ");
        assertEquals("Description New SubTask",
                manager.getSubTask(idSubTask).getDescription(), "Description не обновлен ");
        assertEquals(TaskProgress.IN_PROGRESS, manager.getSubTask(idSubTask).getTaskProgress(),
                "Статус не обновлен ");

    }

    @Test
    void shouldTaskProgressUpdateWhenUpdateSubTaskProgressForOneSubTask() {
        Epic epic = new Epic(1, TaskType.EPIC, "Name Epic"
                , TaskProgress.NEW, "Description Epic");
        int idEpic = manager.addNewEpicTask(epic);
        SubTask updateSubTask = createSubTask(idEpic);

        updateSubTask.setTaskProgress(TaskProgress.IN_PROGRESS);
        manager.updateSubtask(updateSubTask);

        assertEquals(TaskProgress.IN_PROGRESS, epic.getTaskProgress(), "Статус не изменился на IN_PROGRESS");
        updateSubTask.setTaskProgress(TaskProgress.DONE);
        manager.updateSubtask(updateSubTask);
        assertEquals(TaskProgress.DONE, epic.getTaskProgress(), "Статус не изменился на DONE");
    }

    @Test
    void shouldTaskProgressUpdateWhenUpdateSubTaskProgressForMoreSubTask() {
        Epic epic = new Epic(1, TaskType.EPIC, "Name Epic"
                , TaskProgress.NEW, "Description Epic");
        int idEpic = manager.addNewEpicTask(epic);

        SubTask subTaskFirst = new SubTask(2, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Description SubTask", idEpic, timeToFirstTask, duration);
        subTaskFirst.setTaskProgress(TaskProgress.IN_PROGRESS);
        manager.addNewSubTask(subTaskFirst);
        manager.updateSubtask(subTaskFirst);
        SubTask subTaskMiddle = new SubTask(3, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Description SubTask", idEpic, timeToSecondTask, duration);
        manager.addNewSubTask(subTaskMiddle);
        subTaskMiddle.setTaskProgress(TaskProgress.IN_PROGRESS);
        manager.updateSubtask(subTaskMiddle);
        SubTask subTaskLast = new SubTask(4, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Description SubTask", idEpic, timeToThirdTask, duration);
        manager.addNewSubTask(subTaskLast);
        subTaskLast.setTaskProgress(TaskProgress.IN_PROGRESS);
        manager.updateSubtask(subTaskLast);
        assertEquals(TaskProgress.IN_PROGRESS, epic.getTaskProgress(), "Статус не изменился на IN_PROGRESS");
        subTaskFirst.setTaskProgress(TaskProgress.DONE);
        manager.updateSubtask(subTaskFirst);
        subTaskMiddle.setTaskProgress(TaskProgress.DONE);
        manager.updateSubtask(subTaskMiddle);
        subTaskLast.setTaskProgress(TaskProgress.DONE);
        manager.updateSubtask(subTaskLast);
        assertEquals(TaskProgress.DONE, epic.getTaskProgress(), "Статус не изменился на DONE");
    }

    private SubTask createSubTask(int idEpic) {
        SubTask subTask = new SubTask(2, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Description SubTask", idEpic, timeToFirstTask, duration);
        int idSubTask = manager.addNewSubTask(subTask);
        SubTask updateSubTask = new SubTask(idSubTask, TaskType.SUBTASK, "Name New SubTask"
                , TaskProgress.NEW, "Description New SubTask", idEpic, timeToSecondTask, duration);
        return updateSubTask;
    }

    @Test
    void shouldBeRemoveFromHistoryWhenRemoveTask() {
        Task firstTask = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToFirstTask, duration);
        manager.addNewTask(firstTask);
        manager.getTask(1);
        Task secondTask = new Task(2, TaskType.TASK, "Name Task 2"
                , TaskProgress.NEW, "Des Task 2", timeToSecondTask, duration);
        manager.addNewTask(secondTask);
        manager.getTask(2);
        //Длина Истории должна быть 2. Т.к. у нас есть 2 задачи и обе были вызваны методом get
        int sizeHistoryBeforeRemove = 2;
        assertEquals(sizeHistoryBeforeRemove, manager.getHistory().size());
        manager.deleteTask(1);
        // Длина истории должна уменьшится вместе с удалением задачи
        int sizeHistoryAfterRemove = 1;
        assertEquals(sizeHistoryAfterRemove, manager.getHistory().size());

    }

    @Test
    void shouldBeRemoveFromHistoryWhenRemoveEpic() {
        Epic epic1 = new Epic(1, TaskType.EPIC, "Name Epic 1"
                , TaskProgress.NEW, "Des Epic 1");
        manager.addNewEpicTask(epic1);
        manager.getEpicTask(1);
        Epic epic2 = new Epic(2, TaskType.EPIC, "Name Epic 2"
                , TaskProgress.NEW, "Des Epic 2");
        manager.addNewEpicTask(epic2);
        manager.getEpicTask(2);
        //Длина Истории должна быть 2. Т.к. у нас есть 2 эпик и обе были вызваны методом get
        int sizeHistoryBeforeRemove = 2;
        assertEquals(sizeHistoryBeforeRemove, manager.getHistory().size());
        manager.deleteEpicTask(1);
        // Длина истории должна уменьшится вместе с удалением эпика
        int sizeHistoryAfterRemove = 1;
        assertEquals(sizeHistoryAfterRemove, manager.getHistory().size());
    }

    @Test
    void shouldBeRemoveFromHistoryWhenRemoveSubTask() {
        Epic epic1 = new Epic(1, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        manager.addNewEpicTask(epic1);
        SubTask subTask1 = new SubTask(2, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask", epic1.getTaskID(), timeToSecondTask, duration);
        SubTask subTask2 = new SubTask(3, TaskType.SUBTASK, "Name SubTask 2"
                , TaskProgress.NEW, "Des Subtask 2", epic1.getTaskID(), timeToThirdTask, duration);
        manager.addNewSubTask(subTask1);
        manager.addNewSubTask(subTask2);
        manager.getSubTask(subTask1.getTaskID());
        manager.getSubTask(subTask2.getTaskID());
        // Длина Истории должна быть 2. Т.к. у нас есть 3(1 эпик и 2 подзадачи) задачи и две
        //подзадачи были вызваны методом get
        int sizeHistoryBeforeRemove = 2;
        assertEquals(sizeHistoryBeforeRemove, manager.getHistory().size());
        manager.deleteSubtask(2);
        // Длина истории должна уменьшится вместе с удалением подзадачи
        int sizeHistoryAfterRemove = 1;
        assertEquals(sizeHistoryAfterRemove, manager.getHistory().size());
    }

    @Test
    void shouldBeRemoveFromHistoryWhenRemoveTasks() {
        Task firstTask = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToFirstTask, duration);

        manager.addNewTask(firstTask);
        manager.getTask(firstTask.getTaskID());
        Task secondTask = new Task(2, TaskType.TASK, "Name Task 2"
                , TaskProgress.NEW, "Des Task 2", timeToSecondTask, duration);
        manager.addNewTask(secondTask);
        manager.getTask(secondTask.getTaskID());
        //Длина Истории должна быть 2. Т.к. у нас есть 2 задачи и обе были вызваны методом get
        int sizeHistoryBeforeRemove = 2;
        assertEquals(sizeHistoryBeforeRemove, manager.getHistory().size());
        manager.deleteTasks();
        // Длина истории должна уменьшится вместе с удалением всех задач
        int sizeHistoryAfterRemove = 0;
        assertEquals(sizeHistoryAfterRemove, manager.getHistory().size());

    }

    @Test
    void shouldBeRemoveFromHistoryWhenRemoveEpics() {
        Epic epic1 = new Epic(1, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        manager.addNewEpicTask(epic1);
        manager.getEpicTask(epic1.getTaskID());
        Epic epic2 = new Epic(2, TaskType.EPIC, "Name Epic 2", TaskProgress.NEW, "Des Epic 2");
        manager.addNewEpicTask(epic2);
        manager.getEpicTask(epic2.getTaskID());
        SubTask subTask1 = new SubTask(3, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask", epic1.getTaskID(), timeToFirstTask, duration);
        SubTask subTask2 = new SubTask(4, TaskType.SUBTASK, "Name SubTask 2"
                , TaskProgress.NEW, "Des Subtask 2", epic1.getTaskID(), timeToSecondTask, duration);
        manager.addNewSubTask(subTask1);
        manager.addNewSubTask(subTask2);
        manager.getSubTask(subTask1.getTaskID());
        manager.getSubTask(subTask2.getTaskID());
        //Длина Истории должна быть 4. Т.к. у нас есть 2 задачи и 2 подзадачи все они были вызваны методом get
        int sizeHistoryBeforeRemove = 4;
        assertEquals(sizeHistoryBeforeRemove, manager.getHistory().size());
        manager.deleteEpicTasks();
        // Длина истории должна уменьшится вместе с удалением всех эпиков
        int sizeHistoryAfterRemove = 0;
        assertEquals(sizeHistoryAfterRemove, manager.getHistory().size());
    }

    @Test
    void shouldBeRemoveFromHistoryWhenRemoveSubTasks() {
        Epic epic1 = new Epic(1, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        manager.addNewEpicTask(epic1);
        manager.getEpicTask(epic1.getTaskID());
        SubTask subTask1 = new SubTask(2, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask", epic1.getTaskID(), timeToFirstTask, duration);
        SubTask subTask2 = new SubTask(3, TaskType.SUBTASK, "Name SubTask 2"
                , TaskProgress.NEW, "Des Subtask 2", epic1.getTaskID(), timeToSecondTask, duration);
        manager.addNewSubTask(subTask1);
        manager.addNewSubTask(subTask2);
        manager.getSubTask(subTask1.getTaskID());
        manager.getSubTask(subTask2.getTaskID());
        //Длина Истории должна быть 3. Т.к. у нас есть 3(1 эпик и 2 подзадачи) задачи и все были вызваны методом get
        int sizeHistoryBeforeRemove = 3;
        assertEquals(sizeHistoryBeforeRemove, manager.getHistory().size());
        manager.deleteSubTasks();
        // Длина истории должна уменьшится вместе с удалением всех подзадач
        int sizeHistoryAfterRemove = 1;
        assertEquals(sizeHistoryAfterRemove, manager.getHistory().size());
    }

    @Test
    void shouldNotAddTasksWhenTimeOverlaps() {
        // Создаем и добавляем в менеджер задачу
        Task task = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToFirstTask, duration);
        manager.addNewTask(task);
        // Создаем и пытаемся добавить в наш менеджер задачу и подзадачу с тем же временем что и в первой
        Task overlappingTask = new Task(2, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToFirstTask, duration);
        Epic epic = new Epic(3, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        SubTask overlappingSubtask = new SubTask(4, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask"
                , epic.getTaskID(), timeToFirstTask, duration);
        int idOverlappingTask = manager.addNewTask(overlappingTask);
        manager.addNewEpicTask(epic);
        int idOverlappingSubTask = manager.addNewSubTask(overlappingSubtask);

        assertEquals(-1, idOverlappingTask);
        assertEquals(-1, idOverlappingSubTask);
    }

    @Test
    void shouldNotAddTaskWhenStartBeforeAndEndsDuringExistingTask() {
        Duration twoMinutes = Duration.ofMinutes(2);
        Duration fiveMinutes = Duration.ofMinutes(5);
        // Создаем и добавляем в менеджер задачу
        Task task = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToThirdTask, twoMinutes);
        manager.addNewTask(task);
        // Создаем и пытаемся добавить в наш менеджер задачу и подзадачу c временем окончания заходящим в первую задачу
        Task overlappingTask = new Task(2, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToFirstTask, fiveMinutes);
        Epic epic = new Epic(3, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        SubTask overlappingSubtask = new SubTask(4, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask"
                , epic.getTaskID(), timeToFirstTask, fiveMinutes);
        int idOverlappingTask = manager.addNewTask(overlappingTask);
        manager.addNewEpicTask(epic);
        int idOverlappingSubTask = manager.addNewSubTask(overlappingSubtask);

        assertEquals(-1, idOverlappingTask);
        assertEquals(-1, idOverlappingSubTask);
    }

    @Test
    void shouldNotAddTaskWhenStartsDuringExistingTask() {
        Duration treeMinutes = Duration.ofMinutes(3);
        Duration foreMinutes = Duration.ofMinutes(4);
        // Создаем и добавляем в менеджер задачу
        Task task = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToFirstTask, Duration.ofMinutes(3));
        manager.addNewTask(task);
        // Создаем и пытаемся добавить в наш менеджер задачу и подзадачу c временем начала на отрезке первой задачи
        Task overlappingTask = new Task(2, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToSecondTask, Duration.ofMinutes(4));
        Epic epic = new Epic(3, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        SubTask overlappingSubtask = new SubTask(4, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask"
                , epic.getTaskID(), timeToSecondTask, Duration.ofMinutes(4));
        int idOverlappingTask = manager.addNewTask(overlappingTask);
        manager.addNewEpicTask(epic);
        int idOverlappingSubTask = manager.addNewSubTask(overlappingSubtask);

        assertEquals(-1, idOverlappingTask);
        assertEquals(-1, idOverlappingSubTask);
    }

    @Test
    void shouldNotAddTaskWhenFullyOverlapsExistingTask() {
        Duration tenMinutes = Duration.ofMinutes(10);
        // Создаем и добавляем в менеджер задачу
        Task task = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToSecondTask, duration);
        manager.addNewTask(task);
        // Создаем и пытаемся добавить в наш менеджер задачу и подзадачу c временем полностью перекрывающим нашу задачу
        Task overlappingTask = new Task(2, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToFirstTask, tenMinutes);
        Epic epic = new Epic(3, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        SubTask overlappingSubtask = new SubTask(4, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask"
                , epic.getTaskID(), timeToFirstTask, tenMinutes);
        int idOverlappingTask = manager.addNewTask(overlappingTask);
        manager.addNewEpicTask(epic);
        int idOverlappingSubTask = manager.addNewSubTask(overlappingSubtask);

        assertEquals(-1, idOverlappingTask);
        assertEquals(-1, idOverlappingSubTask);
    }

    @Test
    void shouldNotAddTaskWhenIsFullyInsideExistingTask() {
        Duration tenMinutes = Duration.ofMinutes(10);
        // Создаем и добавляем в менеджер задачу
        Task task = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToFirstTask, tenMinutes);
        manager.addNewTask(task);
        // Создаем и пытаемся добавить в наш менеджер задачу и подзадачу c временем полностью находящиеся на отрезке первой
        Task overlappingTask = new Task(2, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToSecondTask, duration);
        Epic epic = new Epic(3, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        SubTask overlappingSubtask = new SubTask(4, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask"
                , epic.getTaskID(), timeToSecondTask, duration);
        int idOverlappingTask = manager.addNewTask(overlappingTask);
        manager.addNewEpicTask(epic);
        int idOverlappingSubTask = manager.addNewSubTask(overlappingSubtask);

        assertEquals(-1, idOverlappingTask);
        assertEquals(-1, idOverlappingSubTask);
    }

    @Test
    void shouldAddTaskWhenNoTimeOverlapWithExistingTask() {
        // Создаем и добавляем в менеджер задачу
        Task task = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToFirstTask, duration);
        manager.addNewTask(task);
        // Создаем и пытаемся добавить в наш менеджер задачу и подзадачу идущие друг за другом
        Task nextTimeTask = new Task(2, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", timeToSecondTask, duration);
        Epic epic = new Epic(3, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        SubTask nextTimeSubTask = new SubTask(4, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask"
                , epic.getTaskID(), timeToThirdTask, duration);
        int idOverlappingTask = manager.addNewTask(nextTimeTask);
        manager.addNewEpicTask(epic);
        int idOverlappingSubTask = manager.addNewSubTask(nextTimeSubTask);

        assertEquals(nextTimeTask.getTaskID(), idOverlappingTask);
        assertEquals(nextTimeSubTask.getTaskID(), idOverlappingSubTask);
    }
}

