package ru.practicum.api.handlers;


import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.exceptons.IncorrectTaskUpdate;
import ru.practicum.manager.general.TaskManager;
import ru.practicum.model.Endpoint;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final int CODE_SUCCESS = 200;
    private static final int CODE_INCORRECT_DATA = 400;
    private static final int CODE_NOT_FOUND = 404;
    private static final int CODE_OVER_LAPS = 406;
    private static final int CODE_INTERNAL_SERVER_ERROR = 500;
    protected final TaskManager manager;
    protected final Gson gson;


    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.manager = taskManager;
        this.gson = gson;
    }

    private void sendText(HttpExchange exchange, String response, int codeResponse) {
        try (OutputStream outputStream = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(codeResponse, 0);
            outputStream.write(response.getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void sendInternalServerError(HttpExchange exchange) {
        sendText(exchange, "Внутренняя ошибка сервера", CODE_INTERNAL_SERVER_ERROR);
    }

    protected void sendNotFound(HttpExchange exchange, String response) {
        sendText(exchange, response, CODE_NOT_FOUND);
    }

    protected void sendIncorrectData(HttpExchange exchange, String response) {
        sendText(exchange, response, CODE_INCORRECT_DATA);
    }

    protected void sendHasOverlaps(HttpExchange exchange, String response) {
        sendText(exchange, response, CODE_OVER_LAPS);
    }

    protected void sendSuccess(HttpExchange exchange, String response) {
        sendText(exchange, response, CODE_SUCCESS);
    }

    protected void sendSuccessWhitOutBody(HttpExchange exchange) {

        try {
            exchange.sendResponseHeaders(201, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        exchange.close();
    }

    protected Optional<Integer> getIDTask(HttpExchange exchange) {
        // Проверка на длину запроса, на случай если в дальнейшем где-то еще будет использоваться метод
        if (getPath(exchange).length < 3) {
            return Optional.empty();
        }
        // Пробуем получить из запроса ID
        try {
            return Optional.of(Integer.parseInt(getPath(exchange)[2]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    protected Endpoint getEndpoint(HttpExchange exchange) {
        // получаем метод запроса
        String method = exchange.getRequestMethod();
        return switch (method) {
            case "GET" -> Endpoint.GET_TASK;
            case "POST" -> Endpoint.POST_TASK;
            case "DELETE" -> Endpoint.DELETE_TASK;
            default -> Endpoint.UNKNOWN;
        };
    }
    protected void validateNotBlankRequest(String request){
        if(request == null || request.isBlank()){
            throw new IncorrectTaskUpdate("Тело запроса пустое, пожалуйста проверьте запрос.");
        }
    }


    protected String[] getPath(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().split("/");
    }
}
