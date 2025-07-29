package ru.practicum.manager.memory;

import org.junit.jupiter.api.Test;
import ru.practicum.manager.TaskManagerTest;
import ru.practicum.manager.general.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.SubTask;
import ru.practicum.model.TaskProgress;
import ru.practicum.model.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }
    @Test
    void shouldReplaceTimeToEpic() {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        InMemoryTaskManager managerMemory = new InMemoryTaskManager();
        Epic epic = new Epic(1, TaskType.EPIC, "Name firstEpic",
                TaskProgress.NEW, "Description firstEpic");
        SubTask subTask = new SubTask(2,TaskType.SUBTASK, "Name subTask",
                TaskProgress.NEW, "Description subTask", epic.getTaskID(),
                now, oneMinutes);
        //manager.(epic);
    }

}

