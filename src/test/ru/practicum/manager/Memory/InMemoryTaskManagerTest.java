package ru.practicum.manager.Memory;

import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskProgress;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    static InMemoryTaskManager manager = new InMemoryTaskManager();

    @Test
    void shouldBeTrueWhenTaskIdMatches() {
        Task taskFirst = new Task("First Task", "Description first Task");
        Task taskSecond = new Task("Second Task", "Description second Task");
        taskFirst.setTaskID(1);
        taskSecond.setTaskID(1);
        assertEquals(taskFirst, taskSecond);
    }

    @Test
    void shouldBeTrueWhenEpicIdMatches() {
        Epic epicFirst = new Epic("First Epic", "Description first Epic");
        Epic epicSecond = new Epic("Second Epic", "Description second Epic");
        epicFirst.setTaskID(1);
        epicSecond.setTaskID(1);
        assertEquals(epicFirst, epicSecond);
    }

    @Test
    void shouldBeTrueWhenSubTaskIdMatches() {
        SubTask subTaskFirst = new SubTask("First SubTask", "Description first SubTask", 1);
        SubTask subTaskSecond = new SubTask("Second SubTask", "Description second SubTask", 1);
        subTaskFirst.setTaskID(1);
        subTaskSecond.setTaskID(1);
        assertEquals(subTaskFirst, subTaskSecond);
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
    void shouldTaskProgressUpdateWhenUpdateSubTaskProgressForOneSubTask(){
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
    void shouldTaskProgressUpdateWhenUpdateSubTaskProgressForMoreSubTask(){
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
    private SubTask createSubTask(int idEpic){
        SubTask subTask = new SubTask("Name SubTask", "Description SubTask", idEpic);
        int idSubTask = manager.addNewSubTask(subTask);
        SubTask updateSubTask = new SubTask("Name New SubTask", "Description New SubTask", idEpic);
        updateSubTask.setTaskID(idSubTask);
        updateSubTask.setEpicTaskID(idEpic);
        return updateSubTask;
    }
}