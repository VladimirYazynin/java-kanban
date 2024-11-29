package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            try {
                if ("GET".equals(exchange.getRequestMethod()))
                    sendText(exchange, gson.toJson(manager.getPrioritizedTasks()));
            } catch (Exception e) {
                handle(exchange, e);
            }
        }
    }

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }
}
