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
            if (exchange.getRequestURI().toString().endsWith("/epic")) {
                sendText(exchange, gson.toJson(manager.getAllEpics()));
            } else {
                Integer id = Integer.parseInt(exchange.getRequestURI().toString().split("/")[3]);
                if (manager.findEpicById(id) == null)
                    throw new NotFoundException("Эпик с " + id + " не найдена");
                sendText(exchange, gson.toJson(manager.findEpicById(id)));
            }
        } catch (Exception e) {
            handle(exchange, e);
        }
    }

    public void handlePostRequest(HttpExchange exchange) {
        try (exchange; InputStream json = exchange.getRequestBody();) {
            Epic epic = gson.fromJson(new String(json.readAllBytes(), UTF), Epic.class);
            if (epic.getId() == null) {
                manager.createTask(epic);
                sendCreated(exchange);
            } else {
                manager.updateTask(epic);
                sendOK(exchange);
            }
        } catch (Exception e) {
            handle(exchange, e);
        }
    }

    public void handleDeleteRequest(HttpExchange exchange) {
        try (exchange; InputStream json = exchange.getRequestBody()) {
            Epic epic = gson.fromJson(new String(json.readAllBytes(), UTF), Epic.class);
            if (manager.findEpicById(epic.getId()) == null)
                throw new NotFoundException("Эпик с " + epic.getId() + " не найдена");
            manager.deleteEpicById(epic.getId());
            sendOK(exchange);
        } catch (Exception e) {
           handle(exchange, e);
        }
    }

    public void handleUnknownRequest(HttpExchange exchange) {
        try (exchange) {
            sendNotAllowed(exchange);
        } catch (Exception e) {
           handle(exchange, e);
        }
    }

    public EpicHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

}
