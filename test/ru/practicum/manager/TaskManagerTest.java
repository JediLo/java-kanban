package ru.practicum.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.exceptons.HasOverLaps;
import ru.practicum.exceptons.NotFoundTasks;
import ru.practicum.manager.general.TaskManager;
import ru.practicum.model.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;


    protected abstract T createManager() throws IOException;


    @BeforeEach
    void setup() throws IOException {
        manager = createManager();
    }

    @Test
    void shouldAddNewTask() {
        // Создаем Task
        Task task = new Task(1, TaskType.TASK, "Name Task",
                TaskProgress.NEW, "Description Task",
                LocalDateTime.now(), Duration.ofMinutes(1));
        // Добавляем в менеджер нашу задачу
        manager.addNewTask(task);
        // Получаем задачу из менеджера по ID
        Task savedTask = manager.getTask(task.getTaskID());
        // Сравниваем созданную нами задачу и полученную из менеджера по всем полям
        equalsTasks(task, savedTask);
    }

    @Test
    void shouldUpdateTask() {
        LocalDateTime now = LocalDateTime.now();
        // Создаем задачу
        Task task = new Task(1, TaskType.TASK, "Name Task",
                TaskProgress.NEW, "Description Task",
                now, Duration.ofMinutes(1));
        // Добавляем в менеджер нашу задачу
        manager.addNewTask(task);
        // Создаем новую задачу с измененными полями(кроме ID) Имитация редактирования всех доступных пользователю полей
        Task updateTask = new Task(1, TaskType.TASK, "Name Update Task",
                TaskProgress.IN_PROGRESS, "Description Update Task",
                now.plusMinutes(2), Duration.ofMinutes(3));
        // Обновляем в менеджере нашу задачу
        manager.updateTask(updateTask);
        // Получаем из менеджера обновленную задачу
        Task savedTask = manager.getTask(updateTask.getTaskID());

        // сравниваем что ID созданной и обновленной задачи одинаковые
        Assertions.assertEquals(task.getTaskID(), savedTask.getTaskID());
        // Сравниваем все поля созданной обновленной задачи и той которая у нас получена из менеджера после обновления
        equalsTasks(savedTask, updateTask);
    }

    @Test
    void shouldDeleteTaskByID() {
        // Создаем задачу
        Task task = new Task(1, TaskType.TASK, "Name Task",
                TaskProgress.NEW, "Description Task",
                LocalDateTime.now(), Duration.ofMinutes(1));
        // Добавляем в менеджер нашу задачу
        manager.addNewTask(task);
        // Удаляем из менеджера
        manager.deleteTask(task.getTaskID());
        // Наш менеджер после удаления не должен иметь задач
        List<Task> tasksFromManager = manager.getAllTask();
        assertEquals(0, tasksFromManager.size());
    }

    @Test
    void shouldReturnAllTasks() {
        LocalDateTime now = LocalDateTime.now();
        // Создаем разные задачи
        Task firstTask = new Task(1, TaskType.TASK, "Name firstTask",
                TaskProgress.NEW, "Description firstTask",
                now, Duration.ofMinutes(1));
        Task secondTask = new Task(2, TaskType.TASK, "Name secondTask",
                TaskProgress.NEW, "Description secondTask",
                now.plusMinutes(1), Duration.ofMinutes(3));
        Task thirdTask = new Task(3, TaskType.TASK, "Name thirdTask",
                TaskProgress.NEW, "Description thirdTask",
                now.plusMinutes(4), Duration.ofMinutes(2));
        // Добавляем в менеджере наши задачи
        manager.addNewTask(firstTask);
        manager.addNewTask(secondTask);
        manager.addNewTask(thirdTask);
        // Получаем список задач из нашего менеджера
        List<Task> savedTasks = manager.getAllTask();
        // Сравниваем созданные нами задачи с теми, которые получены из менеджера
        equalsTasks(savedTasks.getFirst(), firstTask);
        equalsTasks(savedTasks.get(1), secondTask);
        equalsTasks(savedTasks.getLast(), thirdTask);
    }

    @Test
    void shouldClearAllTasksAndSortedTasks() {
        LocalDateTime now = LocalDateTime.now();
        // Создаем разные задачи
        Task firstTask = new Task(1, TaskType.TASK, "Name firstTask",
                TaskProgress.NEW, "Description firstTask",
                now, Duration.ofMinutes(1));
        Task secondTask = new Task(2, TaskType.TASK, "Name secondTask",
                TaskProgress.NEW, "Description secondTask",
                now.plusMinutes(1), Duration.ofMinutes(3));
        Task thirdTask = new Task(3, TaskType.TASK, "Name thirdTask",
                TaskProgress.NEW, "Description thirdTask",
                now.plusMinutes(4), Duration.ofMinutes(2));
        // Добавляем в менеджере наши задачи
        manager.addNewTask(firstTask);
        manager.addNewTask(secondTask);
        manager.addNewTask(thirdTask);
        // Удаляем все задачи из нашего менеджера
        manager.deleteTasks();
        // Получаем список и отсортированный список из нашего менеджера
        List<Task> savedTasks = manager.getAllTask();
        List<Task> sortedSavedTask = manager.getPrioritizedTasks();
        // Проверяем что наши списки полученный из менеджера пусты
        Assertions.assertTrue(savedTasks.isEmpty());
        Assertions.assertTrue(sortedSavedTask.isEmpty());
    }

    @Test
    void shouldAddNewSubTask() {
        // Создаем Task
        Task task = new Task(1, TaskType.TASK, "Name Task",
                TaskProgress.NEW, "Description Task",
                LocalDateTime.now(), Duration.ofMinutes(1));
        // Добавляем в менеджер нашу задачу
        manager.addNewTask(task);
        // Получаем задачу из менеджера по ID
        Task savedTask = manager.getTask(task.getTaskID());
        // Сравниваем созданную нами задачу и полученную из менеджера по всем полям
        equalsTasks(task, savedTask);
    }

    @Test
    void shouldAddNewEpic() {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем Задачи
        Epic epic = new Epic(1, TaskType.EPIC, "Name Epic",
                TaskProgress.NEW, "Description Epic");
        SubTask subTask = new SubTask(2, TaskType.SUBTASK, "Name subTask",
                TaskProgress.NEW, "Description subTask", epic.getTaskID(),
                now, oneMinutes);
        // Добавляем в менеджер нашу задачу и подзадачу
        manager.addNewEpicTask(epic);
        manager.addNewSubTask(subTask);
        // Обновляем время у созданной вручную задачи
        epic.updateTimesEpic(now, oneMinutes, now.plus(oneMinutes));
        // Получаем задачу из менеджера по ID
        Epic savedEpic = manager.getEpicTask(epic.getTaskID());
        // Сравниваем созданную нами задачу и полученную из менеджера по всем полям

        equalsEpic(epic, savedEpic);
    }

    @Test
    void shouldAddSubtaskToEpic() {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем Задачи
        Epic epic = new Epic(1, TaskType.EPIC, "Name Epic",
                TaskProgress.NEW, "Description Epic");
        SubTask subTask = new SubTask(2, TaskType.SUBTASK, "Name subTask",
                TaskProgress.NEW, "Description subTask", epic.getTaskID(),
                now, oneMinutes);
        // Добавляем в менеджер нашу задачу и подзадачу
        manager.addNewEpicTask(epic);
        manager.addNewSubTask(subTask);
        // Получаем задачу из менеджера по ID
        Epic savedEpic = manager.getEpicTask(epic.getTaskID());
        Assertions.assertEquals(savedEpic.getSubTasksID().getFirst(), subTask.getTaskID());
    }

    @Test
    void shouldUpdateEpic() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем задачи
        Epic epic = new Epic(1, TaskType.EPIC, "Name Epic",
                TaskProgress.NEW, "Description Epic");
        SubTask firstSubTask = new SubTask(2, TaskType.SUBTASK, "Name firstSubTask",
                TaskProgress.IN_PROGRESS, "Description firstSubTask", epic.getTaskID(),
                now, oneMinutes);
        SubTask secondSubTask = new SubTask(3, TaskType.SUBTASK, "Name secondSubTask",
                TaskProgress.NEW, "Description secondSubTask", epic.getTaskID(),
                nowPlusOneMinutes, oneMinutes);
        // Добавляем в менеджер наши задачи
        manager.addNewEpicTask(epic);
        manager.addNewSubTask(firstSubTask);
        manager.addNewSubTask(secondSubTask);
        //Создаем новую задачу с измененными полями, кроме ID Имитация редактирования всех доступных пользователю полей
        Epic updateEpic = new Epic(1, TaskType.EPIC, "Name Update Epic",
                TaskProgress.NEW, "Description Update Epic");

        // Обновляем в менеджере нашу задачу
        manager.updateEpic(updateEpic);

        //
        epic.updateTimesEpic(now, oneMinutes.plus(oneMinutes), now.plus(oneMinutes.plus(oneMinutes)));
        epic.addSubTask(firstSubTask);
        epic.addSubTask(secondSubTask);
        // Получаем из менеджера обновленную задачу
        Epic savedEpic = manager.getEpicTask(updateEpic.getTaskID());

        // сравниваем что ID созданной и обновленной задачи одинаковые
        Assertions.assertEquals(epic.getTaskID(), savedEpic.getTaskID());
        // Сравниваем все поля созданной обновленной задачи и той которая у нас получена из менеджера после обновления
        Assertions.assertEquals(epic.getTaskID(), savedEpic.getTaskID());
        Assertions.assertEquals(epic.getType(), savedEpic.getType());
        Assertions.assertNotEquals(epic.getName(), savedEpic.getName());
        Assertions.assertEquals(updateEpic.getTaskProgress(), savedEpic.getTaskProgress());
        Assertions.assertNotEquals(epic.getDescription(), savedEpic.getDescription());
        Assertions.assertEquals(epic.getStartTime(), savedEpic.getStartTime());
        Assertions.assertEquals(epic.getDuration(), savedEpic.getDuration());
        Assertions.assertEquals(epic.getEndTime(), savedEpic.getEndTime());
        Assertions.assertEquals(epic.getSubTasksID(), savedEpic.getSubTasksID());

    }

    @Test
    void shouldDeleteEpicByID() {
        // Создаем задачу
        Epic epic = new Epic(1, TaskType.EPIC, "Name Epic",
                TaskProgress.NEW, "Description Epic");
        // Добавляем в менеджер нашу задачу
        manager.addNewEpicTask(epic);
        // Удаляем из менеджера
        manager.deleteEpicTask(epic.getTaskID());
        // в менеджере не должно остаться эпиков
        Assertions.assertEquals(0, manager.getAllEpic().size());
    }

    @Test
    void shouldReturnAllEpics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        LocalDateTime nowPlusTwoMinutes = now.plusMinutes(2);
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем разные задачи
        // Epic с 1 подзадачей
        Epic firstEpic = new Epic(1, TaskType.EPIC, "Name firstEpic",
                TaskProgress.NEW, "Description firstEpic");
        SubTask subTaskToFirstEpic = new SubTask(2, TaskType.SUBTASK, "Name subTaskToFirstEpic",
                TaskProgress.NEW, "Description subTaskToFirstEpic", firstEpic.getTaskID(),
                now, oneMinutes);
        // Epic с 2 подзадачами
        Epic secondEpic = new Epic(3, TaskType.EPIC, "Name secondEpic",
                TaskProgress.NEW, "Description secondEpic");
        SubTask firstSubTaskToSecondEpic = new SubTask(4, TaskType.SUBTASK,
                "Name firstSubTaskToSecondEpic", TaskProgress.NEW,
                "Description firstSubTaskToSecondEpic", secondEpic.getTaskID(),
                nowPlusOneMinutes, oneMinutes);
        SubTask secondSubTaskToSecondEpic = new SubTask(5, TaskType.SUBTASK,
                "Name secondSubTaskToSecondEpic", TaskProgress.NEW,
                "Description secondSubTaskToSecondEpic", secondEpic.getTaskID(),
                nowPlusTwoMinutes, oneMinutes);
        // Epic без подзадач
        Epic thirdEpic = new Epic(6, TaskType.EPIC, "Name thirdEpic",
                TaskProgress.NEW, "Description thirdEpic");

        // Добавляем в менеджере наши задачи
        manager.addNewEpicTask(firstEpic);
        manager.addNewSubTask(subTaskToFirstEpic);

        manager.addNewEpicTask(secondEpic);
        manager.addNewSubTask(firstSubTaskToSecondEpic);
        manager.addNewSubTask(secondSubTaskToSecondEpic);

        manager.addNewEpicTask(thirdEpic);
        //Обновляем время у наших Epics
        firstEpic.updateTimesEpic(now, oneMinutes, now.plus(oneMinutes));
        secondEpic.updateTimesEpic(nowPlusOneMinutes, oneMinutes.plus(oneMinutes), nowPlusTwoMinutes.plus(oneMinutes));
        // Получаем список задач из нашего менеджера
        List<Epic> savedEpics = manager.getAllEpic();
        // Сравниваем созданные нами задачи с теми, которые получены из менеджера
        equalsEpic(savedEpics.getFirst(), firstEpic);
        equalsEpic(savedEpics.get(1), secondEpic);
        equalsEpic(savedEpics.getLast(), thirdEpic);
    }

    @Test
    void shouldClearAllEpics() {
        // Создаем разные задачи
        Epic firstEpic = new Epic(1, TaskType.EPIC, "Name firstEpic",
                TaskProgress.NEW, "Description firstEpic");
        Epic secondEpic = new Epic(2, TaskType.EPIC, "Name secondEpic",
                TaskProgress.NEW, "Description secondEpic");
        Epic thirdEpic = new Epic(3, TaskType.EPIC, "Name thirdEpic",
                TaskProgress.NEW, "Description thirdEpic");
        // Добавляем в менеджере наши задачи
        manager.addNewEpicTask(firstEpic);
        manager.addNewEpicTask(secondEpic);
        manager.addNewEpicTask(thirdEpic);
        // Удаляем все задачи из нашего менеджера
        manager.deleteEpicTasks();
        // Получаем список из нашего менеджера
        List<Epic> savedEpics = manager.getAllEpic();
        // Проверяем что наш список пуст
        Assertions.assertTrue(savedEpics.isEmpty());

    }

    @Test
    void shouldBeTaskProgressWhenUpdateSubtask() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        LocalDateTime nowPlusTwoMinutes = now.plusMinutes(2);
        Duration oneMinutes = Duration.ofMinutes(1);
        // Epic с 1 подзадачей DONE
        Epic firstEpic = new Epic(1, TaskType.EPIC, "Name firstEpic",
                TaskProgress.IN_PROGRESS, "Description firstEpic");
        SubTask subTaskToFirstEpic = new SubTask(2, TaskType.SUBTASK, "Name subTaskToFirstEpic",
                TaskProgress.DONE, "Description subTaskToFirstEpic", firstEpic.getTaskID(),
                now, oneMinutes);
        // Epic с 2 подзадачами DONE
        Epic secondEpic = new Epic(3, TaskType.EPIC, "Name secondEpic",
                TaskProgress.DONE, "Description secondEpic");
        SubTask firstSubTaskToSecondEpic = new SubTask(4, TaskType.SUBTASK,
                "Name firstSubTaskToSecondEpic", TaskProgress.DONE,
                "Description firstSubTaskToSecondEpic", secondEpic.getTaskID(),
                nowPlusOneMinutes, oneMinutes);
        SubTask secondSubTaskToSecondEpic = new SubTask(5, TaskType.SUBTASK,
                "Name secondSubTaskToSecondEpic", TaskProgress.DONE,
                "Description secondSubTaskToSecondEpic", secondEpic.getTaskID(),
                nowPlusTwoMinutes, oneMinutes);
        // Добавляем в менеджере наши задачи
        manager.addNewEpicTask(firstEpic);
        manager.addNewSubTask(subTaskToFirstEpic);

        manager.addNewEpicTask(secondEpic);
        manager.addNewSubTask(firstSubTaskToSecondEpic);
        manager.addNewSubTask(secondSubTaskToSecondEpic);
        //Обновляем наши Subtasks
        manager.updateSubtask(subTaskToFirstEpic);
        manager.updateSubtask(firstSubTaskToSecondEpic);
        manager.updateSubtask(secondSubTaskToSecondEpic);
        // Проверяем статус наших Эпиков
        Assertions.assertEquals(TaskProgress.DONE,
                manager.getEpicTask(firstEpic.getTaskID()).getTaskProgress());
        Assertions.assertEquals(TaskProgress.DONE,
                manager.getEpicTask(secondEpic.getTaskID()).getTaskProgress());
    }

    @Test
    void shouldBeTaskProgressInProgressWhenUpdateSubtask() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        LocalDateTime nowPlusTwoMinutes = now.plusMinutes(2);
        LocalDateTime nowPlusThreeMinutes = now.plusMinutes(3);
        LocalDateTime nowPlusFourMinutes = now.plusMinutes(4);
        LocalDateTime nowPlusFiveMinutes = now.plusMinutes(5);
        LocalDateTime nowPlusSixMinutes = now.plusMinutes(6);
        Duration oneMinutes = Duration.ofMinutes(1);
        // Epic с 1 подзадачей IN_PROGRESS
        Epic firstEpic = new Epic(1, TaskType.EPIC, "Name firstEpic",
                TaskProgress.NEW, "Description firstEpic");
        SubTask subTaskToFirstEpic = new SubTask(2, TaskType.SUBTASK, "Name subTaskToFirstEpic",
                TaskProgress.IN_PROGRESS, "Description subTaskToFirstEpic", firstEpic.getTaskID(),
                now, oneMinutes);
        // Epic с 2 подзадачами где 1 NEW вторая IN_PROGRESS
        Epic secondEpic = new Epic(3, TaskType.EPIC, "Name secondEpic",
                TaskProgress.NEW, "Description secondEpic");
        SubTask firstSubTaskToSecondEpic = new SubTask(4, TaskType.SUBTASK,
                "Name firstSubTaskToSecondEpic", TaskProgress.NEW,
                "Description firstSubTaskToSecondEpic", secondEpic.getTaskID(),
                nowPlusOneMinutes, oneMinutes);
        SubTask secondSubTaskToSecondEpic = new SubTask(5, TaskType.SUBTASK,
                "Name secondSubTaskToSecondEpic", TaskProgress.IN_PROGRESS,
                "Description secondSubTaskToSecondEpic", secondEpic.getTaskID(),
                nowPlusTwoMinutes, oneMinutes);
        // Epic с 2 подзадачами где 1 NEW вторая DONE
        Epic thirdEpic = new Epic(6, TaskType.EPIC, "Name thirdEpic",
                TaskProgress.NEW, "Description thirdEpic");
        SubTask firstSubTaskToThirdEpic = new SubTask(7, TaskType.SUBTASK,
                "Name firstSubTaskToThirdEpic", TaskProgress.DONE,
                "Description firstSubTaskToThirdEpic", thirdEpic.getTaskID(),
                nowPlusThreeMinutes, oneMinutes);
        SubTask secondSubTaskToThirdEpic = new SubTask(8, TaskType.SUBTASK,
                "Name secondSubTaskToThirdEpic", TaskProgress.NEW,
                "Description secondSubTaskToThirdEpic", thirdEpic.getTaskID(),
                nowPlusFourMinutes, oneMinutes);
        // Epic с 2 подзадачами где 1 DONE вторая IN_PROGRESS
        Epic fourEpic = new Epic(9, TaskType.EPIC, "Name fourEpic",
                TaskProgress.NEW, "Description fourEpic");
        SubTask firstSubTaskToFourEpic = new SubTask(10, TaskType.SUBTASK,
                "Name firstSubTaskToFourEpic", TaskProgress.DONE,
                "Description firstSubTaskToFourEpic", fourEpic.getTaskID(),
                nowPlusFiveMinutes, oneMinutes);
        SubTask secondSubTaskToFourEpic = new SubTask(11, TaskType.SUBTASK,
                "Name secondSubTaskToFourEpic", TaskProgress.IN_PROGRESS,
                "Description secondSubTaskToFourEpic", fourEpic.getTaskID(),
                nowPlusSixMinutes, oneMinutes);
        // Добавляем в менеджере наши задачи
        manager.addNewEpicTask(firstEpic);
        manager.addNewSubTask(subTaskToFirstEpic);

        manager.addNewEpicTask(secondEpic);
        manager.addNewSubTask(firstSubTaskToSecondEpic);
        manager.addNewSubTask(secondSubTaskToSecondEpic);

        manager.addNewEpicTask(thirdEpic);
        manager.addNewSubTask(firstSubTaskToThirdEpic);
        manager.addNewSubTask(secondSubTaskToThirdEpic);

        manager.addNewEpicTask(fourEpic);
        manager.addNewSubTask(firstSubTaskToFourEpic);
        manager.addNewSubTask(secondSubTaskToFourEpic);
        //Обновляем наши Subtasks
        manager.updateSubtask(subTaskToFirstEpic);
        manager.updateSubtask(firstSubTaskToSecondEpic);
        manager.updateSubtask(secondSubTaskToSecondEpic);
        manager.updateSubtask(firstSubTaskToThirdEpic);
        manager.updateSubtask(secondSubTaskToThirdEpic);
        manager.updateSubtask(firstSubTaskToFourEpic);
        manager.updateSubtask(secondSubTaskToFourEpic);
        // Проверяем статус наших Эпиков
        Assertions.assertEquals(TaskProgress.IN_PROGRESS,
                manager.getEpicTask(firstEpic.getTaskID()).getTaskProgress());
        Assertions.assertEquals(TaskProgress.IN_PROGRESS,
                manager.getEpicTask(secondEpic.getTaskID()).getTaskProgress());
        Assertions.assertEquals(TaskProgress.IN_PROGRESS,
                manager.getEpicTask(thirdEpic.getTaskID()).getTaskProgress());
        Assertions.assertEquals(TaskProgress.IN_PROGRESS,
                manager.getEpicTask(fourEpic.getTaskID()).getTaskProgress());

    }

    @Test
    void shouldReturnPrioritizedTasks() {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        LocalDateTime plusOneMinutes = now.plusMinutes(1);
        LocalDateTime plusTwoMinutes = now.plusMinutes(2);
        LocalDateTime plusThreeMinutes = now.plusMinutes(3);
        LocalDateTime plusFourMinutes = now.plusMinutes(4);
        LocalDateTime plusFiveMinutes = now.plusMinutes(5);
        // Создаем разные задачи
        Task firstTaskPlusOneMinutes = new Task(1, TaskType.TASK,
                "Name firstTaskPlusOneMinutes",
                TaskProgress.NEW, "Description firstTaskPlusOneMinutes",
                plusOneMinutes, oneMinutes);
        Task secondTaskNow = new Task(2, TaskType.TASK, "Name secondTaskNow",
                TaskProgress.NEW, "Description secondTaskNow",
                now, oneMinutes);
        Task thirdTaskPlusThreeMinutes = new Task(3, TaskType.TASK,
                "Name thirdTaskPlusThreeMinutes",
                TaskProgress.NEW, "Description thirdTaskPlusThreeMinutes",
                plusThreeMinutes, oneMinutes);
        Epic epic = new Epic(4, TaskType.EPIC, "Name Epic",
                TaskProgress.NEW, "Description Epic");
        SubTask firstSubTaskPlusFiveMinutes = new SubTask(5, TaskType.SUBTASK,
                "Name firstSubTaskPlusFiveMinutes",
                TaskProgress.NEW, "Description firstSubTaskPlusFiveMinutes", epic.getTaskID(),
                plusFiveMinutes, oneMinutes);
        SubTask secondSubTaskPlusFourMinutes = new SubTask(6, TaskType.SUBTASK,
                "Name secondSubTaskPlusFourMinutes",
                TaskProgress.NEW, "Description secondSubTaskPlusFourMinutes", epic.getTaskID(),
                plusFourMinutes, oneMinutes);
        SubTask thirdSubTaskPlusTwoMinutes = new SubTask(7, TaskType.SUBTASK,
                "Name thirdSubTaskPlusTwoMinutes",
                TaskProgress.NEW, "Description thirdSubTaskPlusTwoMinutes", epic.getTaskID(),
                plusTwoMinutes, oneMinutes);
        // Добавляем в менеджере наши задачи
        manager.addNewTask(firstTaskPlusOneMinutes);
        manager.addNewTask(secondTaskNow);
        manager.addNewTask(thirdTaskPlusThreeMinutes);
        manager.addNewEpicTask(epic);
        manager.addNewSubTask(firstSubTaskPlusFiveMinutes);
        manager.addNewSubTask(secondSubTaskPlusFourMinutes);
        manager.addNewSubTask(thirdSubTaskPlusTwoMinutes);
        // Получаем упорядоченный список задач из нашего менеджера
        List<Task> sortedSavedTasks = manager.getPrioritizedTasks();
        // Сравниваем задачи от самой ранней к самой поздней
        equalsTasks(sortedSavedTasks.getFirst(), secondTaskNow);
        equalsTasks(sortedSavedTasks.get(1), firstTaskPlusOneMinutes);
        equalsTasks(sortedSavedTasks.get(2), thirdSubTaskPlusTwoMinutes);
        equalsTasks(sortedSavedTasks.get(3), thirdTaskPlusThreeMinutes);
        equalsTasks(sortedSavedTasks.get(4), secondSubTaskPlusFourMinutes);
        equalsTasks(sortedSavedTasks.getLast(), firstSubTaskPlusFiveMinutes);
    }

    @Test
    void shouldNotAddTasksWhenTimeOverlaps() {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем и добавляем в менеджер задачу
        Task task = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", now, oneMinutes);
        manager.addNewTask(task);
        // Создаем и пытаемся добавить в наш менеджер задачу и подзадачу с тем же временем что и в первой
        Task overlappingTask = new Task(2, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", now, oneMinutes);
        Epic epic = new Epic(3, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        SubTask overlappingSubtask = new SubTask(4, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask"
                , epic.getTaskID(), now, oneMinutes);
        // При добавлении пересекающейся задачи ожидаем ошибку HasOverLaps
        Assertions.assertThrows(HasOverLaps.class,() ->manager.addNewTask(overlappingTask));
        manager.addNewEpicTask(epic);
        // При добавлении пересекающейся задачи ожидаем ошибку HasOverLaps
        Assertions.assertThrows(HasOverLaps.class,() -> manager.addNewSubTask(overlappingSubtask));


    }

    @Test
    void shouldNotAddTaskWhenStartBeforeAndEndsDuringExistingTask() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusTwoMinutes = now.plusMinutes(2);
        Duration twoMinutes = Duration.ofMinutes(2);
        Duration fiveMinutes = Duration.ofMinutes(5);
        // Создаем и добавляем в менеджер задачу
        Task task = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", nowPlusTwoMinutes, twoMinutes);
        manager.addNewTask(task);
        // Создаем и пытаемся добавить в наш менеджер задачу и подзадачу c временем окончания заходящим в первую задачу
        Task overlappingTask = new Task(2, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", now, fiveMinutes);
        Epic epic = new Epic(3, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        SubTask overlappingSubtask = new SubTask(4, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask"
                , epic.getTaskID(), now, fiveMinutes);
        // При добавлении пересекающейся задачи ожидаем ошибку HasOverLaps
        Assertions.assertThrows(HasOverLaps.class,() ->manager.addNewTask(overlappingTask));
        manager.addNewEpicTask(epic);
        // При добавлении пересекающейся задачи ожидаем ошибку HasOverLaps
        Assertions.assertThrows(HasOverLaps.class,() ->manager.addNewSubTask(overlappingSubtask));


    }

    @Test
    void shouldNotAddTaskWhenStartsDuringExistingTask() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        Duration treeMinutes = Duration.ofMinutes(3);
        Duration fourMinutes = Duration.ofMinutes(4);
        // Создаем и добавляем в менеджер задачу
        Task task = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", now, treeMinutes);
        manager.addNewTask(task);
        // Создаем и пытаемся добавить в наш менеджер задачу и подзадачу c временем начала на отрезке первой задачи
        Task overlappingTask = new Task(2, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", nowPlusOneMinutes, fourMinutes);
        Epic epic = new Epic(3, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        SubTask overlappingSubtask = new SubTask(4, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask"
                , epic.getTaskID(), nowPlusOneMinutes, fourMinutes);
        // При добавлении пересекающейся задачи ожидаем ошибку HasOverLaps
        Assertions.assertThrows(HasOverLaps.class,() ->manager.addNewTask(overlappingTask));
        manager.addNewEpicTask(epic);
        // При добавлении пересекающейся задачи ожидаем ошибку HasOverLaps
        Assertions.assertThrows(HasOverLaps.class,() ->manager.addNewSubTask(overlappingSubtask));

    }

    @Test
    void shouldNotAddTaskWhenFullyOverlapsExistingTask() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        Duration oneMinutes = Duration.ofMinutes(1);
        Duration tenMinutes = Duration.ofMinutes(10);
        // Создаем и добавляем в менеджер задачу
        Task task = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", nowPlusOneMinutes, oneMinutes);
        manager.addNewTask(task);
        // Создаем и пытаемся добавить в наш менеджер задачу и подзадачу c временем полностью перекрывающим нашу задачу
        Task overlappingTask = new Task(2, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", now, tenMinutes);
        Epic epic = new Epic(3, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        SubTask overlappingSubtask = new SubTask(4, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask"
                , epic.getTaskID(), nowPlusOneMinutes, tenMinutes);
        // При добавлении пересекающейся задачи ожидаем ошибку HasOverLaps
        Assertions.assertThrows(HasOverLaps.class,() -> manager.addNewTask(overlappingTask));
        manager.addNewEpicTask(epic);
        // При добавлении пересекающейся задачи ожидаем ошибку HasOverLaps
        Assertions.assertThrows(HasOverLaps.class,() ->manager.addNewSubTask(overlappingSubtask));

    }

    @Test
    void shouldNotAddTaskWhenIsFullyInsideExistingTask() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        Duration tenMinutes = Duration.ofMinutes(10);
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем и добавляем в менеджер задачу
        Task task = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", now, tenMinutes);
        manager.addNewTask(task);
        // Создаем и пытаемся добавить в наш менеджер задачу и подзадачу c временем полностью находящиеся на отрезке первой
        Task overlappingTask = new Task(2, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", nowPlusOneMinutes, oneMinutes);
        Epic epic = new Epic(3, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        SubTask overlappingSubtask = new SubTask(4, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask"
                , epic.getTaskID(), nowPlusOneMinutes, oneMinutes);
        // При добавлении пересекающейся задачи ожидаем ошибку HasOverLaps
        Assertions.assertThrows(HasOverLaps.class, () ->manager.addNewTask(overlappingTask));
        manager.addNewEpicTask(epic);
        // При добавлении пересекающейся задачи ожидаем ошибку HasOverLaps
        Assertions.assertThrows(HasOverLaps.class, () ->manager.addNewSubTask(overlappingSubtask));

    }

    @Test
    void shouldAddTaskWhenNoTimeOverlapWithExistingTask() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        LocalDateTime nowPlusTwoMinutes = now.plusMinutes(2);
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем и добавляем в менеджер задачу
        Task task = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", now, oneMinutes);
        manager.addNewTask(task);
        // Создаем и пытаемся добавить в наш менеджер задачу и подзадачу идущие друг за другом
        Task nextTimeTask = new Task(2, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", nowPlusOneMinutes, oneMinutes);
        Epic epic = new Epic(3, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        SubTask nextTimeSubTask = new SubTask(4, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask"
                , epic.getTaskID(), nowPlusTwoMinutes, oneMinutes);
        int idOverlappingTask = manager.addNewTask(nextTimeTask);
        manager.addNewEpicTask(epic);
        int idOverlappingSubTask = manager.addNewSubTask(nextTimeSubTask);

        assertEquals(nextTimeTask.getTaskID(), idOverlappingTask);
        assertEquals(nextTimeSubTask.getTaskID(), idOverlappingSubTask);
    }

    protected static void equalsTasks(Task expected, Task actual) {
        Assertions.assertEquals(expected.getTaskID(), actual.getTaskID());
        Assertions.assertEquals(expected.getType(), actual.getType());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getTaskProgress(), actual.getTaskProgress());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected.getStartTime(), actual.getStartTime());
        Assertions.assertEquals(expected.getDuration(), actual.getDuration());
        Assertions.assertEquals(expected.getEndTime(), actual.getEndTime());
    }

    protected static void equalsSubTasks(SubTask expected, SubTask actual) {
        equalsTasks(expected, actual);
        Assertions.assertEquals(expected.getEpicTaskID(), actual.getEpicTaskID());
    }

    protected static void equalsEpic(Epic expected, Epic actual) {
        equalsTasks(expected, actual);
        Assertions.assertEquals(expected.getSubTasksID(), actual.getSubTasksID());
    }

    @Test
    void shouldBeRemoveFromHistoryWhenRemoveTask() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        Duration oneMinutes = Duration.ofMinutes(1);
        Task firstTask = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", now, oneMinutes);
        manager.addNewTask(firstTask);
        manager.getTask(1);
        Task secondTask = new Task(2, TaskType.TASK, "Name Task 2"
                , TaskProgress.NEW, "Des Task 2", nowPlusOneMinutes, oneMinutes);
        manager.addNewTask(secondTask);
        manager.getTask(secondTask.getTaskID());
        //Длина Истории должна быть 2. Т.к. у нас есть 2 задачи и обе были вызваны методом get
        int sizeHistoryBeforeRemove = 2;
        assertEquals(sizeHistoryBeforeRemove, manager.getHistory().size());
        manager.deleteTask(firstTask.getTaskID());
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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        Duration oneMinutes = Duration.ofMinutes(1);
        Epic epic1 = new Epic(1, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        manager.addNewEpicTask(epic1);
        SubTask subTask1 = new SubTask(2, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask", epic1.getTaskID(), now, oneMinutes);
        SubTask subTask2 = new SubTask(3, TaskType.SUBTASK, "Name SubTask 2"
                , TaskProgress.NEW, "Des Subtask 2", epic1.getTaskID(), nowPlusOneMinutes, oneMinutes);
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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        Duration oneMinutes = Duration.ofMinutes(1);
        Task firstTask = new Task(1, TaskType.TASK, "Name Task"
                , TaskProgress.NEW, "Description Task", now, oneMinutes);

        manager.addNewTask(firstTask);
        manager.getTask(firstTask.getTaskID());
        Task secondTask = new Task(2, TaskType.TASK, "Name Task 2"
                , TaskProgress.NEW, "Des Task 2", nowPlusOneMinutes, oneMinutes);
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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        Duration oneMinutes = Duration.ofMinutes(1);
        Epic epic1 = new Epic(1, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        manager.addNewEpicTask(epic1);
        manager.getEpicTask(epic1.getTaskID());
        Epic epic2 = new Epic(2, TaskType.EPIC, "Name Epic 2", TaskProgress.NEW, "Des Epic 2");
        manager.addNewEpicTask(epic2);
        manager.getEpicTask(epic2.getTaskID());
        SubTask subTask1 = new SubTask(3, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask", epic1.getTaskID(), now, oneMinutes);
        SubTask subTask2 = new SubTask(4, TaskType.SUBTASK, "Name SubTask 2"
                , TaskProgress.NEW, "Des Subtask 2", epic1.getTaskID(), nowPlusOneMinutes, oneMinutes);
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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        Duration oneMinutes = Duration.ofMinutes(1);
        Epic epic1 = new Epic(1, TaskType.EPIC, "Name Epic 1", TaskProgress.NEW, "Des Epic 1");
        manager.addNewEpicTask(epic1);
        manager.getEpicTask(epic1.getTaskID());
        SubTask subTask1 = new SubTask(2, TaskType.SUBTASK, "Name SubTask"
                , TaskProgress.NEW, "Des Subtask", epic1.getTaskID(), now, oneMinutes);
        SubTask subTask2 = new SubTask(3, TaskType.SUBTASK, "Name SubTask 2"
                , TaskProgress.NEW, "Des Subtask 2", epic1.getTaskID(), nowPlusOneMinutes, oneMinutes);
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
