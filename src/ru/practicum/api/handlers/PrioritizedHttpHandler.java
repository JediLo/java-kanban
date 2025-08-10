package ru.practicum.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.exceptons.NotFoundTasks;
import ru.practicum.manager.general.TaskManager;
import ru.practicum.model.Task;

import java.util.List;

public class PrioritizedHttpHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);

    }

    @Override
    public void handle(HttpExchange exchange) {
        String[] path = getPath(exchange);
        // Проверяем запрос и если он не корректный отправляем сообщение об этом
        if (path.length != 2 || !exchange.getRequestMethod().equals("GET")) {
            sendNotFound(exchange, "Такой команды не существует");
            return;
        }
        try {
            // Получаем упорядоченные задачи из менеджера
            List<Task> prioritizedTasks = manager.getPrioritizedTasks();
            // Формируем строку в формате JSON
            String response = gson.toJson(prioritizedTasks);
            // Отправляем ответ
            sendSuccess(exchange, response);
        } catch (NotFoundTasks e) {
            sendNotFound(exchange, e.getMessage());
        }


    }
}
