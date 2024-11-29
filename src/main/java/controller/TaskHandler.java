package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import model.Task;
import service.TaskManager;

import java.io.InputStream;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
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
            if (exchange.getRequestURI().toString().endsWith("/task")) {
                sendText(exchange, gson.toJson(manager.getAllTasks()));
            } else {
                Integer id = Integer.parseInt(exchange.getRequestURI().toString().split("/")[2]);
                if (manager.findTaskById(id) == null)
                    throw new NotFoundException("Задача с " + id + " не найдена");
                sendText(exchange, gson.toJson(manager.findEpicById(id)));
            }
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    private void handlePostRequest(HttpExchange exchange) {
        try (InputStream json = exchange.getRequestBody()) {
            Task task = gson.fromJson(new String(json.readAllBytes(), UTF), Task.class);
            if (task.getId() == null) {
                manager.createTask(task);
                sendStatus(exchange, 201);
            } else {
                manager.updateTask(task);
                sendStatus(exchange, 200);
            }
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) {
        try (InputStream json = exchange.getRequestBody()) {
            Task task = gson.fromJson(new String(json.readAllBytes(), UTF), Task.class);
            if (manager.findTaskById(task.getId()) == null)
                throw new NotFoundException("Задача с " + task.getId() + " не найдена");
            manager.deleteTaskById(task.getId());
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

    public TaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

}
