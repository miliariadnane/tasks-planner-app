package nano.dev.tasksplanner.controller;

import nano.dev.tasksplanner.entity.Task;
import nano.dev.tasksplanner.entity.HttpResponse;
import nano.dev.tasksplanner.entity.enumeration.Type;
import nano.dev.tasksplanner.exception.domain.TaskNotFoundException;
import nano.dev.tasksplanner.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

import static nano.dev.tasksplanner.util.DateUtil.dateTimeFormatter;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping(path = "api/v1/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/all")
    public ResponseEntity<HttpResponse> getTasks() {
        return ResponseEntity.ok().body(taskService.getTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Task>> getTask(@PathVariable Long id) throws TaskNotFoundException {
        return ResponseEntity.ok().body(taskService.getTask(id));
    }

    @PostMapping("/add")
    public ResponseEntity<HttpResponse<Task>> saveTask(@Valid @RequestBody Task task) throws TaskNotFoundException {
        return ResponseEntity.created(
                URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/note/all").toUriString())
        ).body(taskService.saveTask(task));
    }

    @PutMapping("/update")
    public ResponseEntity<HttpResponse<Task>> updateTask(@Valid @RequestBody Task task) throws TaskNotFoundException {
        return ResponseEntity.ok().body(taskService.updateTask(task));
    }

    @GetMapping("/filter")
    public ResponseEntity<HttpResponse<Task>> filterTaskByPriority(
            @RequestParam(value = "type") Type type) {

        return ResponseEntity.ok().body(taskService.filterTasksByType(type));
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<HttpResponse<Task>> deleteTask(@PathVariable(value = "taskId") Long id)
            throws TaskNotFoundException {

        return ResponseEntity.ok().body(taskService.deleteTask(id));
    }

    @RequestMapping("/error")
    public ResponseEntity<HttpResponse<?>> handleError(HttpServletRequest request) {
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .reason("There is no mapping for a " + request.getMethod() + " request for this path on the server")
                        .status(NOT_FOUND)
                        .statusCode(NOT_FOUND.value())
                        .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                        .build(), NOT_FOUND
        );
    }
}
