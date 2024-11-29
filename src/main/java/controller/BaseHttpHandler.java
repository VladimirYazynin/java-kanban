package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ErrorHandler;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected static final Charset UTF = StandardCharsets.UTF_8;
    protected final Gson gson;
    protected final TaskManager manager;
    protected final ErrorHandler errorHandler;


    public void sendText(HttpExchange exchange, String text) throws IOException {
        try (exchange) {
            byte[] resp = text.getBytes(UTF);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(200, resp.length);
            exchange.getResponseBody().write(resp);
        }
    }

    public void sendNotFound() {

    }

    public void sendHasInteractions() {

    }

    public BaseHttpHandler(TaskManager manager, Gson gson, ErrorHandler errorHandler) {
        this.manager = manager;
        this.gson = gson;
        this.errorHandler = errorHandler;
    }

}
