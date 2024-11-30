package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import model.Subtask;
import service.TaskManager;

import java.io.InputStream;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET" -> handleGetRequest(exchange);
            case "POST" -> handlePostRequest(exchange);
            case "DELETE" -> handleDeleteRequest(exchange);
            default -> handleUnknownRequest(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange) {
        try (exchange) {
            if (exchange.getRequestURI().toString().endsWith("/subtasks")) {
                sendText(exchange, gson.toJson(manager.getAllSubtasks()));
            } else {
                Integer id = Integer.parseInt(exchange.getRequestURI().toString().split("/")[2]);
                if (manager.findSubtaskById(id) == null)
                    throw new NotFoundException("Подзадача с " + id + " не найдена");
                sendText(exchange, gson.toJson(manager.findSubtaskById(id)));
            }
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    private void handlePostRequest(HttpExchange exchange) {
        try (InputStream json = exchange.getRequestBody()) {
            Subtask subtask = gson.fromJson(new String(json.readAllBytes(), UTF), Subtask.class);
            if (subtask.getId() == null) {
                manager.createSubtask(subtask);
                sendStatus(exchange, 201);
            } else {
                manager.updateSubtask(subtask);
                sendStatus(exchange, 200);
            }
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) {
        try {
            Integer id = Integer.parseInt(exchange.getRequestURI().toString().split("/")[2]);
            if (manager.findSubtaskById(id) == null)
                throw new NotFoundException("Подзадача с " + id + " не найдена");
            manager.deleteSubtaskById(id);
            sendStatus(exchange, 200);
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    private void handleUnknownRequest(HttpExchange exchange) {
        try (exchange) {
            sendStatus(exchange, 405);
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    public SubtaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }
}
