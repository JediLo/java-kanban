package ru.practicum.manager.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskProgress;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    static InMemoryTaskManager manager;

    @BeforeEach
    void setup() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void shouldAddTaskWithNewStatusAndCorrectId() {

        Task task = new Task("Name Task", "Description Task");
        int idTask = manager.addNewTask(task);
        assertEquals(TaskProgress.NEW, manager.getTask(idTask).getTaskProgress(),
                "Статус не был обновлен на NEW в Task");
        assertEquals(idTask, manager.getTask(idTask).getTaskID(), "Неверный ID в Task");
    }

    @Test
    void shouldAddEpicWithNewStatusAndCorrectId() {

        Epic epic = new Epic("Name Epic", "Description Epic");
        int idEpic = manager.addNewEpicTask(epic);
        assertEquals(TaskProgress.NEW, manager.getEpicTask(idEpic).getTaskProgress(),
                "Статус не был обновлен на NEW в Epic");
        assertEquals(idEpic, manager.getEpicTask(idEpic).getTaskID(), "Неверный ID в Epic");
    }

    @Test
    void shouldAddSubTaskWithNewStatusAndCorrectId() {

        Epic epic = new Epic("Name Epic", "Description Epic");
        int idEpic = manager.addNewEpicTask(epic);
        SubTask subTask = new SubTask("Name Task", "Description Task", idEpic);
        int idSubTask = manager.addNewSubTask(subTask);

        assertEquals(TaskProgress.NEW, manager.getSubTask(idSubTask).getTaskProgress(),
                "Статус не был обновлен на NEW в SubTask");
        assertEquals(idSubTask, manager.getSubTask(idSubTask).getTaskID(), "Неверный ID в SubTask");
    }

    @Test
    void shouldNewNameAndDescriptionAndTaskProgressWhenUpdateTask() {
        Task task = new Task("Name Task", "Description Task");
        int idTask = manager.addNewTask(task);
        Task updateTask = new Task("Name New Task", "Description New Task");
        updateTask.setTaskID(idTask);
        updateTask.setTaskProgress(TaskProgress.IN_PROGRESS);
        manager.updateTask(updateTask);
        assertEquals("Name New Task", manager.getTask(idTask).getName(), "Name не обновлен");
        assertEquals("Description New Task",
                manager.getTask(idTask).getDescription(), "Description не обновлен");
        assertEquals(TaskProgress.IN_PROGRESS, manager.getTask(idTask).getTaskProgress(), "Статус не обновлен");
    }

    @Test
    void shouldNewNameAndDescriptionWhenUpdateEpic() {
        Epic epic = new Epic("Name Epic", "Description Epic");
        int idEpic = manager.addNewEpicTask(epic);
        Epic updateEpic = new Epic("Name New Epic", "Description New Epic");
        updateEpic.setTaskID(idEpic);
        manager.updateEpic(updateEpic);
        assertEquals("Name New Epic", manager.getEpicTask(idEpic).getName(), "Name не обновлен");
        assertEquals("Description New Epic",
                manager.getEpicTask(idEpic).getDescription(), "Description не обновлен");
    }

    @Test
    void shouldNewNameAndDescriptionAndTaskProgressWhenUpdateSubTask() {
        int idEpic = manager.addNewEpicTask(new Epic("Name Epic", "Description Epic"));
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
        Epic epic = new Epic("Name Epic", "Description Epic");
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
        Epic epic = new Epic("Name Epic", "Description Epic");
        int idEpic = manager.addNewEpicTask(epic);

        SubTask subTaskFirst = createSubTask(idEpic);
        subTaskFirst.setTaskProgress(TaskProgress.IN_PROGRESS);
        manager.updateSubtask(subTaskFirst);
        SubTask subTaskMiddle = createSubTask(idEpic);
        SubTask subTaskLast = createSubTask(idEpic);

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
        SubTask subTask = new SubTask("Name SubTask", "Description SubTask", idEpic);
        int idSubTask = manager.addNewSubTask(subTask);
        SubTask updateSubTask = new SubTask("Name New SubTask", "Description New SubTask", idEpic);
        updateSubTask.setTaskID(idSubTask);
        updateSubTask.setEpicTaskID(idEpic);
        return updateSubTask;
    }

    @Test
    void shouldBeRemoveFromHistoryWhenRemoveTask() {
        Task task1 = new Task("Name Task 1", "Des Task 1");
        task1.setTaskID(1);
        manager.addNewTask(task1);
        manager.getTask(1);
        Task task2 = new Task("Name Task 2", "Des Task 2");
        task2.setTaskID(2);
        manager.addNewTask(task2);
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
        Epic epic1 = new Epic("Name Epic 1", "Des Epic 1");
        epic1.setTaskID(1);
        manager.addNewEpicTask(epic1);
        manager.getEpicTask(1);
        Epic epic2 = new Epic("Name Epic 2", "Des Epic 2");
        epic2.setTaskID(2);
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
        Epic epic1 = new Epic("Name Epic 1", "Des Epic 1");
        epic1.setTaskID(1);
        manager.addNewEpicTask(epic1);
        SubTask subTask1 = new SubTask("Name SubTask", "Des Subtask", epic1.getTaskID());
        SubTask subTask2 = new SubTask("Name SubTask 2", "Des Subtask 2", epic1.getTaskID());
        subTask1.setTaskID(2);
        subTask2.setTaskID(3);
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
        Task task1 = new Task("Name Task 1", "Des Task 1");
        task1.setTaskID(1);
        manager.addNewTask(task1);
        manager.getTask(1);
        Task task2 = new Task("Name Task 2", "Des Task 2");
        task2.setTaskID(2);
        manager.addNewTask(task2);
        manager.getTask(2);
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
        Epic epic1 = new Epic("Name Epic 1", "Des Epic 1");
        epic1.setTaskID(1);
        manager.addNewEpicTask(epic1);
        manager.getEpicTask(epic1.getTaskID());
        Epic epic2 = new Epic("Name Epic 2", "Des Epic 2");
        epic2.setTaskID(2);
        manager.addNewEpicTask(epic2);
        manager.getEpicTask(epic2.getTaskID());
        SubTask subTask1 = new SubTask("Name SubTask", "Des Subtask", epic1.getTaskID());
        SubTask subTask2 = new SubTask("Name SubTask 2", "Des Subtask 2", epic1.getTaskID());
        subTask1.setTaskID(3);
        subTask2.setTaskID(4);
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
        Epic epic1 = new Epic("Name Epic 1", "Des Epic 1");
        epic1.setTaskID(1);
        manager.addNewEpicTask(epic1);
        manager.getEpicTask(epic1.getTaskID());
        SubTask subTask1 = new SubTask("Name SubTask", "Des Subtask", epic1.getTaskID());
        SubTask subTask2 = new SubTask("Name SubTask 2", "Des Subtask 2", epic1.getTaskID());
        subTask1.setTaskID(2);
        subTask2.setTaskID(3);
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

}

