package ru.practicum.manager.history;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.manager.general.Managers;
import ru.practicum.model.Epic;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskProgress;

class InMemoryHistoryManagerTest {

    @Test
    void shouldSaveOldVersionTask(){
        HistoryManager manager = Managers.getDefaultHistory();
        Task task = new Task("Name", "Des");
        manager.add(task);
        task.setName("New Name");
        task.setDescription("New Des");
        task.setTaskProgress(TaskProgress.IN_PROGRESS);
        Assertions.assertNotEquals(task.getName() , manager.getHistory().getFirst().getName());
        Assertions.assertNotEquals(task.getDescription() , manager.getHistory().getFirst().getDescription());
        Assertions.assertNotEquals(task.getTaskProgress() , manager.getHistory().getFirst().getTaskProgress());
    }
    @Test
    void shouldSaveOldVersionEpic(){
        HistoryManager manager = Managers.getDefaultHistory();
        Epic epic = new Epic("Name", "Des");
        manager.add(epic);
        epic.setName("New Name");
        epic.setDescription("New Des");
        Assertions.assertNotEquals(epic.getName() , manager.getHistory().getFirst().getName());
        Assertions.assertNotEquals(epic.getDescription() , manager.getHistory().getFirst().getDescription());

    }
    @Test
    void shouldSaveOldVersionSubTask(){
        HistoryManager manager = Managers.getDefaultHistory();
        SubTask subTask = new SubTask("Name", "Des",0);
        manager.add(subTask);
        subTask.setName("New Name");
        subTask.setDescription("New Des");
        subTask.setTaskProgress(TaskProgress.IN_PROGRESS);
        Assertions.assertNotEquals(subTask.getName() , manager.getHistory().getFirst().getName());
        Assertions.assertNotEquals(subTask.getDescription() , manager.getHistory().getFirst().getDescription());
        Assertions.assertNotEquals(subTask.getTaskProgress() , manager.getHistory().getFirst().getTaskProgress());
    }


}