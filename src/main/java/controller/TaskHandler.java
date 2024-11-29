package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ErrorHandler;
import service.TaskManager;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler  implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET" -> handleGetRequest();
            case "POST" -> handlePostRequest();
            case "DELETE" -> handleDeleteRequest();
            default -> sendNotFound();
        }


    }

    private static void handleGetRequest() {

    }

    public static void handlePostRequest() {

    }

    public static void handleDeleteRequest() {

    }

    public TaskHandler(TaskManager manager, Gson gson, ErrorHandler errorHandler) {
        super(manager, gson, errorHandler);
    }

}
