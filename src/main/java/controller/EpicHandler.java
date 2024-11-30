package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import model.Epic;
import service.TaskManager;

import java.io.InputStream;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
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
            if (exchange.getRequestURI().toString().endsWith("/epics")) {
                sendText(exchange, gson.toJson(manager.getAllEpics()));
            } else {
                Integer id = Integer.parseInt(exchange.getRequestURI().toString().split("/")[2]);
                if (manager.findEpicById(id) == null)
                    throw new NotFoundException("Эпик с " + id + " не найдена");
                sendText(exchange, gson.toJson(manager.findEpicById(id)));
            }
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    private void handlePostRequest(HttpExchange exchange) {
        try (InputStream json = exchange.getRequestBody()) {
            Epic epic = gson.fromJson(new String(json.readAllBytes(), UTF), Epic.class);
            if (epic.getId() == null) {
                manager.createEpic(epic);
                sendStatus(exchange, 201);
            } else {
                manager.updateEpic(epic);
                sendStatus(exchange, 200);
            }
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) {
        try {
            Integer id = Integer.parseInt(exchange.getRequestURI().toString().split("/")[2]);
            if (manager.findEpicById(id) == null)
                throw new NotFoundException("Эпик с " + id + " не найдена");
            manager.deleteEpicById(id);
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

    public EpicHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

}
