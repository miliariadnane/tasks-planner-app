package nano.dev.tasksplanner.service;

import nano.dev.tasksplanner.exception.domain.TaskNotFoundException;
import nano.dev.tasksplanner.exception.domain.UserNotFoundException;

public interface DetachUserService {

    boolean detachUserFromTask(long taskId, long userId)
            throws TaskNotFoundException, UserNotFoundException;
}
