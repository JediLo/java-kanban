package ru.practicum.api.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.exceptons.HasOverLaps;
import ru.practicum.exceptons.IncorrectTaskUpdate;
import ru.practicum.exceptons.NotFoundTasks;
import ru.practicum.manager.general.TaskManager;
import ru.practicum.model.Endpoint;
import ru.practicum.model.Epic;
import ru.practicum.model.SubTask;
import ru.practicum.model.Task;

import java.util.List;
import java.util.Optional;

public class EpicHttpHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) {
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
                        validateNotBlankRequest(request);
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
                Optional<Integer> optionalID = getIDTask(exchange);
                if (optionalID.isPresent()) {
                    int id = optionalID.get();
                    switch (endpoint) {
                        case GET_TASK -> {
                            sendSuccess(exchange, gson.toJson(manager.getEpicTask(id)));
                            return;
                        }
                        case DELETE_TASK -> {
                            manager.deleteEpicTask(id);
                            sendSuccessWhitOutBody(exchange);
                            return;
                        }

                    }
                }
            } else if (path.length == 4) {
                Optional<Integer> optionalID = getIDTask(exchange);
                if (optionalID.isPresent()) {
                    int id = optionalID.get();
                    if (endpoint == Endpoint.GET_TASK && path[3].equals("subtasks")){
                        List<SubTask> subTaskList = manager.getSubTasksFromEpic(id);
                        sendSuccess(exchange ,gson.toJson(subTaskList, new TypeToken<List<SubTask>>() {
                        }.getType()));
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
            sendInternalServerError(exchange);
        }
    }
}
