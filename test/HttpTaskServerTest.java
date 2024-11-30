import com.google.gson.Gson;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {

    HttpTaskServer taskServer = new HttpTaskServer();
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerTest() throws IOException {

    }

    @BeforeEach
    void setUp() {
        taskServer.getManager().deleteAllTasks();
        taskServer.getManager().deleteAllSubtasks();
        taskServer.getManager().deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    void shutDown() {
        taskServer.stop();
    }

    @Test
    void testGetTaskById() throws IOException, InterruptedException {
        Task task1 = new Task(0, "Учёба", "Пройти новую тему", TaskStatus.NEW, LocalDateTime.now(), 5);
        Task task2 = new Task(1, "Уборка", "Помыть пол", TaskStatus.IN_PROGRESS, LocalDateTime.now().plusMinutes(10), 30);
        String task2Json = gson.toJson(task2);
        taskServer.getManager().createTask(task1);
        taskServer.getManager().createTask(task2);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(task2Json, response.body());

    }

    @Test
    void testGetAllTask() throws IOException, InterruptedException {
        Task task1 = new Task("Учёба", "Пройти новую тему", TaskStatus.NEW, LocalDateTime.now(), 5);
        Task task2 = new Task("Уборка", "Помыть пол", TaskStatus.IN_PROGRESS, LocalDateTime.now().plusMinutes(10), 30);
        taskServer.getManager().createTask(task1);
        taskServer.getManager().createTask(task2);
        String taskJsonList = gson.toJson(taskServer.getManager().getAllTasks());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(taskJsonList, response.body());
    }

    @Test
    void testCreateTask() throws IOException, InterruptedException {
        assertEquals(0, taskServer.getManager().getAllTasks().size());
        Task task1 = new Task("Учёба", "Пройти новую тему", TaskStatus.NEW, LocalDateTime.now(), 5);
        String taskJson = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskServer.getManager().getAllTasks().size());
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task task1 = new Task("Учёба", "Пройти новую тему", TaskStatus.NEW, LocalDateTime.now(), 5);
        taskServer.getManager().createTask(task1);
        assertEquals(1, taskServer.getManager().getAllTasks().size());
        Task updatedTask = new Task(0, "Учёба", "Повторить материал",
                TaskStatus.IN_PROGRESS, LocalDateTime.now().plusMinutes(10), 5);
        String updatedTaskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(updatedTaskJson, gson.toJson(taskServer.getManager().getTaskById(0)));
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task task1 = new Task("Учёба", "Пройти новую тему", TaskStatus.NEW, LocalDateTime.now(), 5);
        taskServer.getManager().createTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(true, response.body().isEmpty());
    }

    @Test
    void shouldReturnStatus404() throws IOException, InterruptedException {
        Task task1 = new Task(0, "Учёба", "Пройти новую тему", TaskStatus.NEW, LocalDateTime.now(), 5);
        taskServer.getManager().createTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldReturnStatus406() throws IOException, InterruptedException {
        Task task1 = new Task("Учёба", "Пройти новую тему", TaskStatus.NEW, LocalDateTime.now(), 30);
        Task task2 = new Task("Уборка", "Помыть пол", TaskStatus.IN_PROGRESS, LocalDateTime.now(), 10);
        String task1Json = gson.toJson(task1);
        String task2Json = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .header("Content-Type", "application/json")
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    void shouldReturnStatus500() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/get");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(500, response.statusCode());
    }

}
