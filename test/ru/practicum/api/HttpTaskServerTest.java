package ru.practicum.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.api.server.HttpTaskServer;
import ru.practicum.manager.general.TaskManager;
import ru.practicum.manager.memory.InMemoryTaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    final TaskManager manager = new InMemoryTaskManager();
    final HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
    final Gson gson = httpTaskServer.getGson();

    HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        manager.deleteTasks();
        manager.deleteEpicTasks();
        manager.deleteSubTasks();
        httpTaskServer.start();
    }

    @AfterEach
    void shutDown() {
        httpTaskServer.stop();
    }

    // POST without parameters
    @Test
    void shouldAddTask() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем задачу
        Task task = new Task("Name Task", "Description Task", now, oneMinutes);
        // Конвертируем в Json
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTask();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Name Task", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Description Task", tasksFromManager.getFirst().getDescription(), "Некорректное имя задачи");

    }

    @Test
    void shouldAddEpic() throws IOException, InterruptedException {
        // Создаем эпик
        Epic epic = new Epic("Epic Name", "Epic Description");
        // Конвертируем в Json
        String taskJson = gson.toJson(epic);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpic();


        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic Name", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Epic Description", tasksFromManager.getFirst().getDescription(), "Некорректное имя задачи");
    }

    @Test
    void shouldAddSubTask() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем эпик
        Epic epic = new Epic("Epic Name", "Epic Description");
        // Добавляем в наш менеджер, иначе невозможно будет добавить подзадачу
        int epicID = manager.addNewEpicTask(epic);
        // Создаем Подзадачу
        SubTask subTask = new SubTask("Name SubTask", "Description SubTask", now, oneMinutes, epicID);
        // Конвертируем в Json
        String taskJson = gson.toJson(subTask);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        List<SubTask> tasksFromManager = manager.getAllSubTask();


        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Name SubTask", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Description SubTask", tasksFromManager.getFirst().getDescription(), "Некорректное имя задачи");
    }

    @Test
    void shouldReturnErrorWhenAddTaskFromBadJson() throws IOException, InterruptedException {

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString("[{name}]")).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(400, response.statusCode());
        assertEquals("Ошибка синтаксиса JSON", response.body());

    }

    @Test
    void shouldReturnErrorWhenAddSubTaskFromBadJson() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString("[{name}]")).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(400, response.statusCode());
        assertEquals("Ошибка синтаксиса JSON", response.body());
    }

    @Test
    void shouldReturnErrorWhenAddEpicFromBadJson() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString("[{name}]")).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(400, response.statusCode());
        assertEquals("Ошибка синтаксиса JSON", response.body());
    }

    @Test
    void shouldNotAddTaskWhenOverLaps() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);

        // Создаем задачу
        Task task = new Task("Name Task", "Description Task", now, oneMinutes);
        // добавляем задачу в менеджер
        manager.addNewTask(task);
        // Конвертируем в Json
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());
        assertEquals("Время выполнения задачи, пересекается с другой задачей", response.body());

    }

    @Test
    void shouldNotAddSubTaskWhenOverLaps() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем эпик
        Epic epic = new Epic("Epic Name", "Epic Description");
        // Добавляем в наш менеджер, иначе невозможно будет добавить подзадачу
        int epicID = manager.addNewEpicTask(epic);
        // Создаем Подзадачу
        SubTask subTask = new SubTask("Name SubTask", "Description SubTask", now, oneMinutes, epicID);
        // Добавим в наш менеджер подзадачу
        manager.addNewSubTask(subTask);
        // Конвертируем в Json
        String taskJson = gson.toJson(subTask);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());
        assertEquals("Время выполнения подзадачи, пересекается с другой задачей", response.body());
    }


    // POST with parameters
    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем задачу
        Task task = new Task("Name Task", "Description Task", now, oneMinutes);
        // Добавим нашу задачу в менеджер
        int taskID = manager.addNewTask(task);
        // Создаем вторую задачу с отредактированным именем и описанием
        Task newTask = new Task("New Name Task", "New Description Task", now, oneMinutes);
        // Конвертируем в Json

        String taskJson = gson.toJson(newTask);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskID);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTask();


        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("New Name Task", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("New Description Task", tasksFromManager.getFirst().getDescription(), "Некорректное имя задачи");
    }

    @Test
    void shouldUpdateSubTask() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем эпик
        Epic epic = new Epic("Epic Name", "Epic Description");
        // Добавляем в наш менеджер, иначе невозможно будет добавить подзадачу
        int epicID = manager.addNewEpicTask(epic);
        // Создаем Подзадачу
        SubTask subTask = new SubTask("Name SubTask", "Description SubTask", now, oneMinutes, epicID);
        // Добавляем в наш менеджер подзадачу
        int subTaskID = manager.addNewSubTask(subTask);
        // Создаем новую подзадачу с обновленными полями
        SubTask newSubTask = new SubTask("New Name SubTask", "New Description SubTask", now, oneMinutes, epicID);
        // Конвертируем в Json
        String taskJson = gson.toJson(newSubTask);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTaskID);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<SubTask> tasksFromManager = manager.getAllSubTask();


        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("New Name SubTask", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("New Description SubTask", tasksFromManager.getFirst().getDescription(), "Некорректное имя задачи");
    }

    @Test
    void shouldReturnErrorWhenUpdateTaskAndTaskNotInManager() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем задачу
        Task task = new Task("Name Task", "Description Task", now, oneMinutes);
        // Конвертируем в Json
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Задачи с ID 1 не существует.", response.body());

    }

    @Test
    void shouldReturnErrorWhenUpdateSubTaskAndSubTaskNotInManager() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем эпик
        Epic epic = new Epic("Epic Name", "Epic Description");
        // Добавляем в наш менеджер, иначе невозможно будет добавить подзадачу
        int epicID = manager.addNewEpicTask(epic);
        // Создаем Подзадачу
        SubTask subTask = new SubTask("Name SubTask", "Description SubTask", now, oneMinutes, epicID);
        // Конвертируем в Json
        String taskJson = gson.toJson(subTask);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Подзадачи с ID 1 не существует.", response.body());

    }

    // GET without parameters
    @Test
    void shouldReturnListTask() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        LocalDateTime nowPlusTwoMinutes = now.plusMinutes(2);
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем задачи и помещаем их в manager
        Task task1 = new Task("Name Task1", "Description Task1", now, oneMinutes);
        Task task2 = new Task("Name Task2", "Description Task2", nowPlusOneMinutes, oneMinutes);
        Task task3 = new Task("Name Task3", "Description Task3", nowPlusTwoMinutes, oneMinutes);
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewTask(task3);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(3, tasksFromResponse.size(), "Некорректное количество задач");
        assertEquals("Name Task1", tasksFromResponse.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Name Task2", tasksFromResponse.get(1).getName(), "Некорректное имя задачи");
        assertEquals("Name Task3", tasksFromResponse.getLast().getName(), "Некорректное имя задачи");


    }

    @Test
    void shouldReturnListEpic() throws IOException, InterruptedException {

        // Создаем эпики и помещаем их в manager
        Epic epic1 = new Epic("Name Epic1", "Description Epic1");
        Epic epic2 = new Epic("Name Epic2", "Description Epic2");
        Epic epic3 = new Epic("Name Epic3", "Description Epic3");
        manager.addNewEpicTask(epic1);
        manager.addNewEpicTask(epic2);
        manager.addNewEpicTask(epic3);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Epic> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());
        assertEquals(3, tasksFromResponse.size(), "Некорректное количество задач");
        assertEquals("Name Epic1", tasksFromResponse.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Name Epic2", tasksFromResponse.get(1).getName(), "Некорректное имя задачи");
        assertEquals("Name Epic3", tasksFromResponse.getLast().getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldReturnListSubTask() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        LocalDateTime nowPlusTwoMinutes = now.plusMinutes(2);
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем эпик и  подзадачи и помещаем их в manager
        Epic epic = new Epic("Name Epic", "Description Epic");
        int epicID = manager.addNewEpicTask(epic);
        SubTask subTask1 = new SubTask("Name SubTask1", "Description Task1",
                now, oneMinutes, epicID);
        SubTask subTask2 = new SubTask("Name SubTask2", "Description SubTask2",
                nowPlusOneMinutes, oneMinutes, epicID);
        SubTask subTask3 = new SubTask("Name SubTask3", "Description SubTask3",
                nowPlusTwoMinutes, oneMinutes, epicID);
        manager.addNewSubTask(subTask1);
        manager.addNewSubTask(subTask2);
        manager.addNewSubTask(subTask3);


        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<SubTask> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {
        }.getType());
        assertEquals(3, tasksFromResponse.size(), "Некорректное количество задач");
        assertEquals("Name SubTask1", tasksFromResponse.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Name SubTask2", tasksFromResponse.get(1).getName(), "Некорректное имя задачи");
        assertEquals("Name SubTask3", tasksFromResponse.getLast().getName(), "Некорректное имя задачи");

    }

    @Test
    void shouldReturnEmptyListTask() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(0, tasksFromResponse.size(), "Некорректное количество задач");


    }

    @Test
    void shouldReturnEmptyListEpic() throws IOException, InterruptedException {

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Epic> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());
        assertEquals(0, tasksFromResponse.size(), "Некорректное количество задач");
    }

    @Test
    void shouldReturnEmptyListSubTask() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<SubTask> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {
        }.getType());

        assertEquals(0, tasksFromResponse.size(), "Некорректное количество задач");

    }


    // Get with parameters
    @Test
    void shouldReturnTask() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        LocalDateTime nowPlusTwoMinutes = now.plusMinutes(2);
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем задачи и помещаем их в manager
        Task task1 = new Task("Name Task1", "Description Task1", now, oneMinutes);
        Task task2 = new Task("Name Task2", "Description Task2", nowPlusOneMinutes, oneMinutes);
        Task task3 = new Task("Name Task3", "Description Task3", nowPlusTwoMinutes, oneMinutes);
        manager.addNewTask(task1);
        int taskID = manager.addNewTask(task2);
        manager.addNewTask(task3);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskID);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertEquals("Name Task2", taskFromResponse.getName(), "Некорректное имя задачи");


    }

    @Test
    void shouldReturnEpic() throws IOException, InterruptedException {

        // Создаем эпики и помещаем их в manager
        Epic epic1 = new Epic("Name Epic1", "Description Epic1");
        Epic epic2 = new Epic("Name Epic2", "Description Epic2");
        Epic epic3 = new Epic("Name Epic3", "Description Epic3");
        manager.addNewEpicTask(epic1);
        int epicID = manager.addNewEpicTask(epic2);
        manager.addNewEpicTask(epic3);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicID);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        Epic taskFromResponse = gson.fromJson(response.body(), Epic.class);
        assertEquals("Name Epic2", taskFromResponse.getName(), "Некорректное имя задачи");

    }

    @Test
    void shouldReturnSubTask() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        LocalDateTime nowPlusTwoMinutes = now.plusMinutes(2);
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем эпик и  подзадачи и помещаем их в manager
        Epic epic = new Epic("Name Epic", "Description Epic");
        int epicID = manager.addNewEpicTask(epic);
        SubTask subTask1 = new SubTask("Name SubTask1", "Description Task1",
                now, oneMinutes, epicID);
        SubTask subTask2 = new SubTask("Name SubTask2", "Description SubTask2",
                nowPlusOneMinutes, oneMinutes, epicID);
        SubTask subTask3 = new SubTask("Name SubTask3", "Description SubTask3",
                nowPlusTwoMinutes, oneMinutes, epicID);
        manager.addNewSubTask(subTask1);
        int subTaskID = manager.addNewSubTask(subTask2);
        manager.addNewSubTask(subTask3);


        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTaskID);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа

        assertEquals(200, response.statusCode());

        SubTask taskFromResponse = gson.fromJson(response.body(), SubTask.class);

        assertEquals("Name SubTask2", taskFromResponse.getName(), "Некорректное имя задачи");


    }

    @Test
    void shouldReturnSubTasksFomEpic() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        LocalDateTime nowPlusTwoMinutes = now.plusMinutes(2);
        Duration oneMinutes = Duration.ofMinutes(1);
        // Создаем эпик и  подзадачи и помещаем их в manager
        Epic epic = new Epic("Name Epic", "Description Epic");
        int epicID = manager.addNewEpicTask(epic);
        SubTask subTask1 = new SubTask("Name SubTask1", "Description Task1",
                now, oneMinutes, epicID);
        SubTask subTask2 = new SubTask("Name SubTask2", "Description SubTask2",
                nowPlusOneMinutes, oneMinutes, epicID);
        SubTask subTask3 = new SubTask("Name SubTask3", "Description SubTask3",
                nowPlusTwoMinutes, oneMinutes, epicID);
        manager.addNewSubTask(subTask1);
        manager.addNewSubTask(subTask2);
        manager.addNewSubTask(subTask3);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicID + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа

        assertEquals(200, response.statusCode());

        List<SubTask> taskFromResponse = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {
        }.getType());

        assertEquals(3, taskFromResponse.size());
        assertEquals("Name SubTask1", taskFromResponse.getFirst().getName());
        assertEquals("Name SubTask2", taskFromResponse.get(1).getName());
        assertEquals("Name SubTask3", taskFromResponse.getLast().getName());
    }


    @Test
    void shouldReturnErrorWhenGetTaskAndTaskNotInManager() throws IOException, InterruptedException {

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Задачи с ID 1 не существует.", response.body());

    }

    @Test
    void shouldReturnErrorWhenGetEpicAndEpicNotInManager() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа и возвращаемую ошибку
        assertEquals(404, response.statusCode());
        assertEquals("Эпика с ID 1 не существует.", response.body());
    }

    @Test
    void shouldReturnErrorWhenGetSubTaskAndSubTaskNotInManager() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа и возвращаемую ошибку
        assertEquals(404, response.statusCode());
        assertEquals("Подзадачи с ID 1 не существует.", response.body());

    }

    // History and Prioritized
    @Test
    void shouldReturnEmptyHistoryListWhenNoTasksWereAccessed() throws IOException, InterruptedException {

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что вернулась пустота
        assertEquals("[]", response.body());


    }

    @Test
    void shouldReturnEmptyPrioritizedListWhenNoTasksWereAccessed() throws IOException, InterruptedException {

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что вернулась пустота
        assertEquals("[]", response.body());
    }

    @Test
    void shouldReturnHistoryList() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);

        Duration oneMinutes = Duration.ofMinutes(1);
        // Создадим и добавим в наш менеджер разные задачи

        Epic epic = new Epic("Name Epic", "Description Epic");
        int epicID = manager.addNewEpicTask(epic);
        SubTask subTask = new SubTask("Name SubTask1", "Description Task1",
                now, oneMinutes, epicID);
        Task task = new Task("Name Task1", "Description Task1", nowPlusOneMinutes, oneMinutes);
        int taskID = manager.addNewTask(task);
        int subTaskID = manager.addNewSubTask(subTask);
        // Вызовем задачи из нашего менеджера, чтобы они попали в историю просмотра
        manager.getSubTask(subTaskID);
        manager.getTask(taskID);
        manager.getEpicTask(epicID);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(200, response.statusCode());

        assertEquals(3, tasksFromResponse.size(), "Некорректное количество задач");
        assertEquals("Name SubTask1", tasksFromResponse.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Name Task1", tasksFromResponse.get(1).getName(), "Некорректное имя задачи");
        assertEquals("Name Epic", tasksFromResponse.getLast().getName(), "Некорректное имя задачи");


    }

    @Test
    void shouldReturnPrioritizedList() throws IOException, InterruptedException {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneMinutes = now.plusMinutes(1);
        LocalDateTime nowPlusTwoMinutes = now.plusMinutes(2);

        Duration oneMinutes = Duration.ofMinutes(1);
        // Создадим и добавим в наш менеджер разные задачи

        Epic epic = new Epic("Name Epic", "Description Epic");
        int epicID = manager.addNewEpicTask(epic);
        SubTask subTask = new SubTask("Name SubTask1", "Description Task1",
                nowPlusTwoMinutes, oneMinutes, epicID);
        Task task1 = new Task("Name Task1", "Description Task1", nowPlusOneMinutes, oneMinutes);
        Task task2 = new Task("Name Task2", "Description Task2", now, oneMinutes);
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewSubTask(subTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(200, response.statusCode());

        assertEquals(3, tasksFromResponse.size(), "Некорректное количество задач");
        assertEquals("Name Task2", tasksFromResponse.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Name Task1", tasksFromResponse.get(1).getName(), "Некорректное имя задачи");
        assertEquals("Name SubTask1", tasksFromResponse.getLast().getName(), "Некорректное имя задачи");


    }

    // Запросы Post без тела
    @Test
    void shouldFailWhenPostingTaskWithoutBody() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.noBody()).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(400, response.statusCode());
        assertEquals("Тело запроса пустое, пожалуйста проверьте запрос.", response.body());
    }

    @Test
    void shouldFailWhenPostingSubTaskWithoutBody() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.noBody()).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(400, response.statusCode());
        assertEquals("Тело запроса пустое, пожалуйста проверьте запрос.", response.body());
    }
}