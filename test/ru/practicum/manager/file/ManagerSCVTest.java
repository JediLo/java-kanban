package ru.practicum.manager.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagerSCVTest {

    @Test
    void shouldBeSCVStringFromTask() {
        Task task = new Task(1, TaskType.TASK,"Name", TaskProgress.IN_PROGRESS,"description");
        List<Task> taskList = new ArrayList<>();
        taskList.add(task);
        String stringSCV= ManagerSCV.getSCVFromTasks(taskList);
        String stringResult = ManagerSCV.title + System.lineSeparator() + "1,TASK,Name,IN_PROGRESS,description, ";
        Assertions.assertEquals(stringSCV, stringResult);
    }
    @Test
    void shouldBeNullFromSCVTitle() {
        Task stringSCV =  ManagerSCV.getTaskFromString( ManagerSCV.title);
        assertNull(stringSCV);
    }
    @Test
    void shouldBeTaskFromSCVString() {
        Task task = new Task(1, TaskType.TASK,"Name", TaskProgress.IN_PROGRESS,"description");
        Task stringSCV =  ManagerSCV.getTaskFromString("1,TASK,Name,IN_PROGRESS,description, ");
        Assertions.assertEquals(task, stringSCV);
    }
    @Test
    void shouldBeEpicFromSCVString() {
        Epic epic = new Epic(1, TaskType.EPIC,"Name", TaskProgress.IN_PROGRESS,"description");
        Epic stringSCV =  (Epic) ManagerSCV.getTaskFromString( "1,EPIC,Name,IN_PROGRESS,description, ");
        Assertions.assertEquals(epic, stringSCV);
    }
    @Test
    void shouldBeSubTaskFromSCVString() {
        SubTask subTask = new SubTask(1, TaskType.SUBTASK,"Name", TaskProgress.IN_PROGRESS,"description", 2);
        SubTask stringSCV =   (SubTask) ManagerSCV.getTaskFromString( "1,SUBTASK,Name,IN_PROGRESS,description,2");
        Assertions.assertEquals(subTask, stringSCV);
    }
}