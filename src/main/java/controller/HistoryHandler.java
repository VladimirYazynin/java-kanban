package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            try {
                if ("GET".equals(exchange.getRequestMethod()))
                    sendText(exchange, gson.toJson(manager.getHistory()));
            } catch (Exception e) {
               handle(exchange, e);
            }
        }
    }

    public HistoryHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }
}
