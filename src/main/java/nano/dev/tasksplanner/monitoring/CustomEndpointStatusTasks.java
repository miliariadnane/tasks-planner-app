package nano.dev.tasksplanner.monitoring;

import nano.dev.tasksplanner.repository.TaskRepository;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Endpoint(id = "count-tasks-by-status")
@Component
public class CustomEndpointStatusTasks {

    private final TaskRepository taskRepository;

    public CustomEndpointStatusTasks(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /* count tasks by status */
    @ReadOperation
    public Map<String, Integer> countTasksByStatus() {
        Map<String, Integer> mapForTypes = new HashMap<>();
        mapForTypes.put("NOT_STARTED", taskRepository.countByStatus(4));
        mapForTypes.put("IN_PROGRESS", taskRepository.countByStatus(3));
        mapForTypes.put("DONE", taskRepository.countByStatus(2));
        mapForTypes.put("CLOSED", taskRepository.countByStatus(1));
        return mapForTypes;
    }

}
