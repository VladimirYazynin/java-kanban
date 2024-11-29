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
            if (exchange.getRequestURI().toString().endsWith("/subtask")) {
                sendText(exchange, gson.toJson(manager.getAllSubtasks()));
            } else {
                Integer id = Integer.parseInt(exchange.getRequestURI().toString().split("/")[3]);
                if (manager.findSubtaskById(id) == null)
                    throw new NotFoundException("Подзадача с " + id + " не найдена");
                sendText(exchange, gson.toJson(manager.findSubtaskById(id)));
            }
        } catch (Exception e) {
            handle(exchange, e);
        }
    }

    public void handlePostRequest(HttpExchange exchange) {
        try (exchange; InputStream json = exchange.getRequestBody();) {
            Subtask subtask = gson.fromJson(new String(json.readAllBytes(), UTF), Subtask.class);
            if (subtask.getId() == null) {
                manager.createSubtask(subtask);
                sendCreated(exchange);
            } else {
                manager.updateTask(subtask);
                sendOK(exchange);
            }
        } catch (Exception e) {
           handle(exchange, e);
        }
    }

    public void handleDeleteRequest(HttpExchange exchange) {
        try (exchange; InputStream json = exchange.getRequestBody();) {
            Subtask subtask = gson.fromJson(new String(json.readAllBytes(), UTF), Subtask.class);
            if (manager.findSubtaskById(subtask.getId()) == null)
                throw new NotFoundException("Подзадача с " + subtask.getId() + " не найдена");
            manager.deleteSubtaskById(subtask.getId());
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

    public SubtaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }
}
