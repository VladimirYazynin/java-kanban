import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> tasksHistory = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if (tasksHistory.size() == 10) {
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }
}
