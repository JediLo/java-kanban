package ru.practicum.api.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.api.Adapters.DurationAdapter;
import ru.practicum.api.Adapters.LocalDateTimeAdapter;
import ru.practicum.api.Handlers.*;
import ru.practicum.manager.general.Managers;
import ru.practicum.manager.general.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;

    private final HttpServer httpServer;
    private final TaskManager manager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.manager = taskManager;
        this.gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter()).create();
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        createContext();
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
    }

    private void createContext() {
        httpServer.createContext("/tasks", new TaskHttpHandler(manager, gson));
        httpServer.createContext("/epics", new EpicHttpHandler(manager, gson));
        httpServer.createContext("/subtasks", new SubTaskHttpHandler(manager, gson));
        httpServer.createContext("/history", new HistoryHttpHandler(manager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHttpHandler(manager, gson));
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен.");
    }

    public Gson getGson() {
        return gson;
    }
}
