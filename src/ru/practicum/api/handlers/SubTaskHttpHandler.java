package ru.practicum.api.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.exceptons.HasOverLaps;
import ru.practicum.exceptons.IncorrectTaskUpdate;
import ru.practicum.exceptons.NotFoundTasks;
import ru.practicum.manager.general.TaskManager;
import ru.practicum.model.Endpoint;
import ru.practicum.model.SubTask;

import java.util.Optional;

public class SubTaskHttpHandler extends BaseHttpHandler implements HttpHandler {
    public SubTaskHttpHandler(TaskManager taskManager, Gson gson) {
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
                        sendSuccess(exchange, gson.toJson(manager.getAllSubTask()));
                        return;
                    }
                    case POST_TASK -> {
                        validateNotBlankRequest(request);
                        SubTask subTask = gson.fromJson(request, SubTask.class);
                        manager.addNewSubTask(subTask);
                        sendSuccessWhitOutBody(exchange);
                        return;
                    }
                    case DELETE_TASK -> {
                        manager.deleteSubTasks();
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
                            sendSuccess(exchange, gson.toJson(manager.getSubTask(id)));
                            return;
                        }
                        case POST_TASK -> {
                            validateNotBlankRequest(request);
                            SubTask subTask = gson.fromJson(request, SubTask.class);
                            subTask.setTaskID(id);
                            manager.updateSubtask(subTask);
                            sendSuccessWhitOutBody(exchange);
                            return;
                        }
                        case DELETE_TASK -> {
                            manager.deleteSubtask(id);
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
            sendInternalServerError(exchange);
        }
    }
}
