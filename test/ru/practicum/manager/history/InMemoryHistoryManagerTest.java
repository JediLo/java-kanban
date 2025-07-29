package ru.practicum.manager.history;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.manager.general.Managers;
import ru.practicum.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class InMemoryHistoryManagerTest {
    private HistoryManager manager;

    @BeforeEach
    void setup() {
        manager = Managers.getDefaultHistory();
    }

    @Test
    void shouldSaveOldVersionTask() {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        Task task = new Task(1, TaskType.TASK, "Name"
                , TaskProgress.NEW, "Des", now, oneMinutes);
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
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        SubTask subTask = new SubTask(1, TaskType.SUBTASK, "Name"
                , TaskProgress.NEW, "Des", 0, now, oneMinutes);
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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        Duration oneMinutes = Duration.ofMinutes(1);
        // Проверят, что задачи не дублируются и добавляются в конец
        Task task = new Task(1, TaskType.TASK, "Name"
                , TaskProgress.NEW, "Des", now, oneMinutes);
        manager.add(task);
        Task task2 = new Task(2, TaskType.TASK, "Name task 2"
                , TaskProgress.NEW, "Des task 2", nowPlusOneMinutes, oneMinutes);
        manager.add(task2);
        // Сначала первой в истории должна быть первая задача.
        Assertions.assertEquals(task, manager.getHistory().getFirst());
        Assertions.assertNotEquals(task, manager.getHistory().getLast());
        manager.add(task);
        // После второго вызова добавления в историю, первая задача должна переместиться в конец. А из начала уйти.
        Assertions.assertNotEquals(task, manager.getHistory().getFirst());
        Assertions.assertEquals(task, manager.getHistory().getLast());
    }

    @Test
    void shouldNotHistory() {
        // Пустая история
        List<Task> history = manager.getHistory();
        Assertions.assertEquals(0, history.size());
    }

    @Test
    void shouldRemoveFromTailHistory() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        Duration oneMinutes = Duration.ofMinutes(1);

        Task task = new Task(1, TaskType.TASK, "Name"
                , TaskProgress.NEW, "Des", now, oneMinutes);
        manager.add(task);
        Task task2 = new Task(2, TaskType.TASK, "Name task 2"
                , TaskProgress.NEW, "Des task 2", nowPlusOneMinutes, oneMinutes);
        manager.add(task2);
        manager.remove(task2.getTaskID());
        Assertions.assertNotEquals(manager.getHistory().getLast(), task2);
    }

    @Test
    void shouldRemoveFromHeadHistory() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        Duration oneMinutes = Duration.ofMinutes(1);

        Task task = new Task(1, TaskType.TASK, "Name"
                , TaskProgress.NEW, "Des", now, oneMinutes);
        manager.add(task);
        Task task2 = new Task(2, TaskType.TASK, "Name task 2"
                , TaskProgress.NEW, "Des task 2", nowPlusOneMinutes, oneMinutes);
        manager.add(task2);
        manager.remove(task.getTaskID());
        Assertions.assertNotEquals(manager.getHistory().getFirst(), task);
    }

    @Test
    void shouldRemoveFromMiddleHistory() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        LocalDateTime nowPlusThreeMinutes = now.plusMinutes(1);
        Duration oneMinutes = Duration.ofMinutes(1);

        Task task = new Task(1, TaskType.TASK, "Name"
                , TaskProgress.NEW, "Des", now, oneMinutes);
        manager.add(task);
        Task task2 = new Task(2, TaskType.TASK, "Name task 2"
                , TaskProgress.NEW, "Des task 2", nowPlusOneMinutes, oneMinutes);
        manager.add(task2);
        Task task3 = new Task(3, TaskType.TASK, "Name task 3"
                , TaskProgress.NEW, "Des task 3", nowPlusThreeMinutes, oneMinutes);
        manager.add(task3);
        manager.remove(task2.getTaskID());
        // Размер уменьшился с 3 до 2 задач
        Assertions.assertEquals(2, manager.getHistory().size());
        Assertions.assertNotEquals(manager.getHistory().getFirst(), task2);
        Assertions.assertNotEquals(manager.getHistory().getLast(), task2);
    }
}