package nano.dev.tasksplanner.controller;

import lombok.AllArgsConstructor;
import nano.dev.tasksplanner.entity.DetachUserRequest;
import nano.dev.tasksplanner.entity.HttpResponse;
import nano.dev.tasksplanner.exception.domain.TaskNotFoundException;
import nano.dev.tasksplanner.exception.domain.UserNotFoundException;
import nano.dev.tasksplanner.service.DetachUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "api/v1/detachUser")
@AllArgsConstructor
public class DetachUserController {

    private DetachUserService detachUserService;

    @PostMapping
    public ResponseEntity<HttpResponse> detachUserFromTask(@RequestBody DetachUserRequest detachUserRequest)
            throws UserNotFoundException, TaskNotFoundException {

        boolean isDetached = detachUserService.detachUserFromTask(
                detachUserRequest.getTaskId(),
                detachUserRequest.getUserId()
        );

        String response;
        if(isDetached) {
            response = "User detached from task successfully";
        } else {
            response = "Nothing detached !!";
        }

        return response(OK, response);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                        message), httpStatus);
    }
}
