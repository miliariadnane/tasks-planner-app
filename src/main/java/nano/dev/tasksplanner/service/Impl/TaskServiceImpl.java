package nano.dev.tasksplanner.service.Impl;

import lombok.extern.slf4j.Slf4j;
import nano.dev.tasksplanner.entity.HttpResponse;
import nano.dev.tasksplanner.entity.Task;
import nano.dev.tasksplanner.entity.User;
import nano.dev.tasksplanner.entity.enumeration.Type;
import nano.dev.tasksplanner.exception.domain.TaskNotFoundException;
import nano.dev.tasksplanner.repository.TaskRepository;
import nano.dev.tasksplanner.service.TaskService;
import nano.dev.tasksplanner.service.WebhookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;
import static java.util.Optional.ofNullable;
import static nano.dev.tasksplanner.util.DateUtil.dateTimeFormatter;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Service
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final WebhookService webhookService;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            WebhookService webhookService) {
        this.taskRepository = taskRepository;
        this.webhookService = webhookService;
    }

    @Override
    public HttpResponse<Task> getTasks() {
        log.info("Fetching all the tasks from the database");
        return HttpResponse.<Task>builder()
                .tasks(taskRepository.findAll()
                        .stream()
                        .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
                        .collect(Collectors.toList())) /* sort data from the latest until oldest */
                .message(taskRepository.count() > 0 ? taskRepository.count() + " tasks retrieved" : "No tasks to display")
                .status(OK)
                .statusCode(OK.value())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build();
    }

    @Override
    public Optional<Task> getTask(long taskId) throws TaskNotFoundException {
        log.info("Retrieve specific task from the database");
        return ofNullable(taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("The task was not found on the server")));
    }

    @Override
    public HttpResponse<Task> saveTask(Task task) throws TaskNotFoundException {
        log.info("Saving new task to the database");

        task.setTaskId(UUID.randomUUID());
        task.setCreatedAt(LocalDateTime.now());

        Set<User> users = new HashSet<>();

        task.getUsers()
                .stream()
                .map(user -> {
                    users.add(user);
                    task.addUser(user);
                    return users;
                })
                .collect(Collectors.toSet());

        if (users.size() > 0) {
            taskRepository.saveAndFlush(task);
        }

        webhookService.sendWebhook(
                task.getTitle(),
                task.getType(),
                task.getPriority(),
                task.getStatus(),
                task.getId(),
                users.stream()
                        .map(user -> user.getDiscordAccount())
                        .collect(Collectors.toList())
        );

        return HttpResponse.<Task>builder()
                .tasks(singleton(task))
                .message("Task created successfully")
                .status(CREATED)
                .statusCode(CREATED.value())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build();
    }

    @Override
    public HttpResponse<Task> updateTask(Task task) throws TaskNotFoundException {
        log.info("Updating task to the database");
        Optional<Task> currentTask = ofNullable(taskRepository.findById(task.getId())
                .orElseThrow(() -> new TaskNotFoundException("The task was not found on the server")));

        Task updateTask = currentTask.get();
        updateTask.setId(task.getId());
        updateTask.setTitle(task.getTitle());
        updateTask.setDetails(task.getDetails());
        updateTask.setPriority(task.getPriority());
        updateTask.setStatus(task.getStatus());
        updateTask.setType(task.getType());
        updateTask.setStartDate(task.getStartDate());
        updateTask.setEndDate(task.getEndDate());

        taskRepository.save(updateTask);
        return HttpResponse.<Task>builder()
                .tasks(singleton(updateTask))
                .message("Task updated successfully")
                .status(OK)
                .statusCode(OK.value())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build();
    }

    @Override
    public HttpResponse<Task> filterTasksByType(Type type) {
        List<Task> tasks = taskRepository.findByType(type);
        log.info("Filtering tasks by type {}", type);
        return HttpResponse.<Task>builder()
                .tasks(tasks)
                .message(tasks.size() + " tasks are of " + type)
                .status(OK)
                .statusCode(OK.value())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build();
    }

    @Override
    public HttpResponse<Task> deleteTask(Long id) throws TaskNotFoundException {
        log.info("Deleting task from the database by id {}", id);
        Optional<Task> deletedTask = ofNullable(taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("The task was not found on the server")));

        deletedTask.ifPresent(taskRepository::delete);
        return HttpResponse.<Task>builder()
                .tasks(singleton(deletedTask.get()))
                .message("Task deleted successfully")
                .status(OK)
                .statusCode(OK.value())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build();
    }
}
