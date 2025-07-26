package ru.practicum.manager.history;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.manager.general.Managers;
import ru.practicum.model.*;

import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryHistoryManagerTest {
    private HistoryManager manager;
    LocalDateTime timeToFirstTask = LocalDateTime.now();
    LocalDateTime timeToSecondTask = timeToFirstTask.plusMinutes(2);
    Duration duration = Duration.ofMinutes(1);

    @BeforeEach
    void setup() {
        manager = Managers.getDefaultHistory();
    }

    @Test
    void shouldSaveOldVersionTask() {
        Task task = new Task(1, TaskType.TASK, "Name"
                , TaskProgress.NEW, "Des", timeToFirstTask, duration);
        manager.add(task);
        task.setName("New Name");
        task.setDescription("New Des");
        task.setTaskProgress(TaskProgress.IN_PROGRESS);
        Assertions.assertNotEquals(task.getName(), manager.getHistory().getFirst().getName());
        Assertions.assertNotEquals(task.getDescription(), manager.getHistory().getFirst().getDescription());
        Assertions.assertNotEquals(task.getTaskProgress(), manager.getHistory().getFirst().getTaskProgress());
    }

    @Test
    void shouldSaveOldVersionEpic() {
        Epic epic = new Epic(1, TaskType.EPIC, "Name"
                , TaskProgress.NEW, "Des");
        manager.add(epic);
        epic.setName("New Name");
        epic.setDescription("New Des");
        Assertions.assertNotEquals(epic.getName(), manager.getHistory().getFirst().getName());
        Assertions.assertNotEquals(epic.getDescription(), manager.getHistory().getFirst().getDescription());

    }

    @Test
    void shouldSaveOldVersionSubTask() {
        SubTask subTask = new SubTask(1, TaskType.SUBTASK, "Name"
                , TaskProgress.NEW, "Des", 0, timeToFirstTask, duration);
        manager.add(subTask);
        subTask.setName("New Name");
        subTask.setDescription("New Des");
        subTask.setTaskProgress(TaskProgress.IN_PROGRESS);
        Assertions.assertNotEquals(subTask.getName(), manager.getHistory().getFirst().getName());
        Assertions.assertNotEquals(subTask.getDescription(), manager.getHistory().getFirst().getDescription());
        Assertions.assertNotEquals(subTask.getTaskProgress(), manager.getHistory().getFirst().getTaskProgress());
    }

    @Test
    void shouldBeTaskUpdate() {
        Task task = new Task(1, TaskType.TASK, "Name"
                , TaskProgress.NEW, "Des", timeToFirstTask, duration);
        manager.add(task);
        Task task2 = new Task(2, TaskType.TASK, "Name task 2"
                , TaskProgress.NEW, "Des task 2", timeToSecondTask, duration);
        manager.add(task2);
        // Сначала первой в истории должна быть первая задача.
        Assertions.assertEquals(task, manager.getHistory().getFirst());
        Assertions.assertNotEquals(task, manager.getHistory().getLast());
        manager.add(task);
        // После второго вызова добавления в историю, первая задача должна переместиться в конец. А из начала уйти.
        Assertions.assertNotEquals(task, manager.getHistory().getFirst());
        Assertions.assertEquals(task, manager.getHistory().getLast());
    }

}