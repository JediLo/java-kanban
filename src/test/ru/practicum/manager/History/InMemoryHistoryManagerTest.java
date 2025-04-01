package ru.practicum.manager.History;

import org.junit.jupiter.api.Test;
import ru.practicum.manager.General.Managers;
import ru.practicum.model.Task;
import ru.practicum.model.TaskProgress;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void shouldSaveOldVersionTask(){
        HistoryManager manager = Managers.getDefaultHistory();
        Task task = new Task("Name", "Des");
        manager.add(task);
        task.setName("New Name");
        task.setDescription("New Des");
        task.setTaskProgress(TaskProgress.IN_PROGRESS);
        assertNotEquals(task.getName() , manager.getHistory().getFirst().getName());
        assertNotEquals(task.getDescription() , manager.getHistory().getFirst().getDescription());
        assertNotEquals(task.getTaskProgress() , manager.getHistory().getFirst().getTaskProgress());



    }

}