package nano.dev.tasksplanner.entity;

import lombok.Data;

@Data
public class DetachUserRequest {

    private long taskId;
    private long userId;
}
