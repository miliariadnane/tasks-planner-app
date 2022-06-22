package nano.dev.tasksplanner.monitoring;

import nano.dev.tasksplanner.repository.TaskRepository;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Endpoint(id = "count-tasks-by-type")
@Component
public class CustomEndpointTypeTasks {

    private final TaskRepository taskRepository;

    public CustomEndpointTypeTasks(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /* count tasks by type */
    @ReadOperation
    public Map<String, Integer> countTasksByType() {
        Map<String, Integer> mapForTypes = new HashMap<>();
        mapForTypes.put("DOCUMENTATION", taskRepository.countByType(5)); // 5 is the value of Type.DOCUMENTATION
        mapForTypes.put("TEST", taskRepository.countByType(4)); // 4 is the value of Type.TEST
        mapForTypes.put("IMPROVEMENT", taskRepository.countByType(3));
        mapForTypes.put("BUG", taskRepository.countByType(2));
        mapForTypes.put("FEATURE", taskRepository.countByType(1));
        return mapForTypes;
    }

}
