package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ErrorHandler;
import service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            try {
                if ("GET".equals(exchange.getRequestMethod()))
                    sendText(exchange, gson.toJson(manager.getPrioritizedTasks()));
            } catch (Exception e) {
                errorHandler.handle(exchange, e);
            }
        }
    }

    public PrioritizedHandler(TaskManager manager, Gson gson, ErrorHandler errorHandler) {
        super(manager, gson, errorHandler);
    }
}
