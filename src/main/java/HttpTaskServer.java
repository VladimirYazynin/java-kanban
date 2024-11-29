import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import controller.*;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private final static int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer server;
    private final Gson gson;


    public static void main(String[] args) {
        HttpTaskServer taskServer = new HttpTaskServer();
        taskServer.start();
    }

    public void start() {
        System.out.println("Запуск сервера на " + PORT + " порту");
        server.start();
    }

    public void stop() {
        System.out.println("Остановка сервера на " + PORT + " порту");
        server.stop(0);
    }

    public HttpTaskServer() {
        manager = Managers.getDefault();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/task", new TaskHandler(manager, gson));
        server.createContext("/epic", new EpicHandler(manager, gson));
        server.createContext("/subtask", new SubtaskHandler(manager, gson));
        server.createContext("/history", new HistoryHandler(manager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(manager, gson));
    }

}
