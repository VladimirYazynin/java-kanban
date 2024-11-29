package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ManagerSaveException;
import exception.NotFoundException;
import exception.ValidationException;
import service.TaskManager;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected static final Charset UTF = StandardCharsets.UTF_8;
    protected final Gson gson;
    protected final TaskManager manager;

    protected void sendText(HttpExchange exchange, String text) {
        try (exchange) {
            byte[] resp = text.getBytes(UTF);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(200, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    protected void sendStatus(HttpExchange exchange, int rCode) {
        try (exchange) {
            exchange.sendResponseHeaders(rCode, 0);
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    protected void sendTextAndStatus(HttpExchange exchange, int rCode, String text) {
        try (exchange) {
            byte[] resp = text.getBytes(UTF);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(rCode, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    public void handleException(HttpExchange exchange, Exception e) {
        try {
            if (e instanceof ManagerSaveException) {
                e.printStackTrace();
                sendTextAndStatus(exchange, 500, gson.toJson(e.getMessage()));
                return;
            }

            if (e instanceof NotFoundException) {
                e.printStackTrace();
                sendTextAndStatus(exchange, 404, gson.toJson(e.getMessage()));
                return;
            }

            if (e instanceof ValidationException) {
                e.printStackTrace();
                sendTextAndStatus(exchange, 406, "Ошибка");
                return;
            }

            e.printStackTrace();
            sendTextAndStatus(exchange, 500, gson.toJson(e.getMessage()));
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    public BaseHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

}
