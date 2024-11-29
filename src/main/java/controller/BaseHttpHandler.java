package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ManagerSaveException;
import exception.NotFoundException;
import exception.ValidationException;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected static final Charset UTF = StandardCharsets.UTF_8;
    protected final Gson gson;
    protected final TaskManager manager;

    public void sendText(HttpExchange exchange, String text) {
        try (exchange) {
            byte[] resp = text.getBytes(UTF);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(200, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (Exception e) {
            handle(exchange, e);
        }
    }

    public void sendCreated(HttpExchange exchange) {
        try (exchange) {
            exchange.sendResponseHeaders(201, 0);
        } catch (Exception e) {
           handle(exchange, e);
        }
    }

    public void sendOK(HttpExchange exchange) {
        try (exchange) {
            exchange.sendResponseHeaders(200, 0);
        } catch (Exception e) {
            handle(exchange, e);
        }
    }

    public void sendNotAllowed(HttpExchange exchange) {
        try (exchange) {
            exchange.sendResponseHeaders(405, 0);
        } catch (Exception e) {
           handle(exchange, e);
        }
    }

    public void handle(HttpExchange exchange, Exception e) {
        try {
            if (e instanceof ManagerSaveException) {
                //
                return;
            }

            if (e instanceof NotFoundException) {
                //
                return;
            }

            if (e instanceof ValidationException) {
                e.printStackTrace();
                //
            }

        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    public void sendNotFound(HttpExchange exchange, NotFoundException e) throws IOException {

    }

    public void sendHasInteractions() {

    }

    public void sendServerError() {

    }


    public BaseHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

}
