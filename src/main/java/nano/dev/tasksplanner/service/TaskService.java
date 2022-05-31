package nano.dev.tasksplanner.service;

import nano.dev.tasksplanner.entity.HttpResponse;
import nano.dev.tasksplanner.entity.Task;
import nano.dev.tasksplanner.entity.enumeration.Type;
import nano.dev.tasksplanner.exception.domain.TaskNotFoundException;

import java.util.Optional;

public interface TaskService {
    HttpResponse<Task> getTasks();

    Optional<Task> getTask(long taskId) throws TaskNotFoundException;

    HttpResponse<Task> saveTask(Task task) throws TaskNotFoundException;

    HttpResponse<Task> updateTask(Task task) throws TaskNotFoundException;

    HttpResponse<Task> filterTasksByType(Type type);

    HttpResponse<Task> deleteTask(Long id) throws TaskNotFoundException;
}
