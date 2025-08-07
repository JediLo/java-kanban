package ru.practicum.api.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.exceptons.HasOverLaps;
import ru.practicum.exceptons.IncorrectTaskUpdate;
import ru.practicum.exceptons.NotFoundTasks;
import ru.practicum.manager.general.TaskManager;
import ru.practicum.model.Endpoint;
import ru.practicum.model.Epic;

import java.io.IOException;
import java.util.Optional;

public class EpicHttpHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Получаем endpoint
        Endpoint endpoint = getEndpoint(exchange);
        // Получаем запрос
        String[] path = getPath(exchange);
        try {
            // Получаем тело запроса
            String request = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            if (path.length == 2) {
                switch (endpoint) {
                    case GET_TASK -> {
                        sendSuccess(exchange, gson.toJson(manager.getAllEpic()));
                        return;
                    }
                    case POST_TASK -> {
                        Epic epic = gson.fromJson(request, Epic.class);
                        manager.addNewEpicTask(epic);
                        sendSuccessWhitOutBody(exchange);
                        return;
                    }
                    case DELETE_TASK -> {
                        manager.deleteEpicTasks();
                        sendSuccessWhitOutBody(exchange);
                        return;
                    }
                }
            } else if (path.length == 3) {
                Optional<Integer> IDOptional = getIDTask(exchange);
                if (IDOptional.isPresent()) {
                    int id = IDOptional.get();
                    switch (endpoint) {
                        case GET_TASK -> {
                            sendSuccess(exchange, gson.toJson(manager.getEpicTask(id)));
                            return;
                        }
                        case POST_TASK -> {
                           ;
                            Epic epic = gson.fromJson(request, Epic.class);
                            epic.setTaskID(id);
                            manager.updateEpic(epic);
                            sendSuccessWhitOutBody(exchange);
                            return;
                        }
                        case DELETE_TASK -> {
                            manager.deleteEpicTask(id);
                            sendSuccessWhitOutBody(exchange);
                            return;
                        }

                    }
                }
            }
            //Если ни один case не сработал
            throw new NotFoundTasks("Такой команды не существует");

        } catch (HasOverLaps e) {
            sendHasOverlaps(exchange, e.getMessage());
        } catch (IncorrectTaskUpdate e) {
            sendIncorrectData(exchange, e.getMessage());
        } catch (NotFoundTasks e) {
            sendNotFound(exchange, e.getMessage());
        } catch (JsonSyntaxException e) {
            sendIncorrectData(exchange, "Ошибка синтаксиса JSON");
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange);
        }
    }
}
