package nano.dev.tasksplanner.controller;

import lombok.AllArgsConstructor;
import nano.dev.tasksplanner.entity.HttpResponse;
import nano.dev.tasksplanner.entity.User;
import nano.dev.tasksplanner.exception.domain.*;
import nano.dev.tasksplanner.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "api/v1/users")
@AllArgsConstructor
public class UserController {

    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";

    private UserService userService;

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping(
            path = "/add",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<User> addNewUser(
            @RequestBody @Valid User user
    ) throws UserNotFoundException, UsernameExistException, EmailExistsException {

        User newUser = userService.addNewUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping(
            path = "/update",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<User> updateUser(
            @RequestBody User user
    ) throws UserNotFoundException, UsernameExistException, EmailExistsException {

        User updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }


    @PostMapping(path = "/update/profile")
    public ResponseEntity<User> updateUserProfile(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            @RequestParam("enable") String enable,
            @RequestParam("job") String job,
            @RequestParam("discordAccount") String discordAccount

    ) throws UserNotFoundException, UsernameExistException, EmailExistsException {

        User updatedUserProfile = userService.updateUserProfile(
                currentUsername, firstName, lastName, username, email, role, Boolean.parseBoolean(enable), job, discordAccount
        );
        return new ResponseEntity<>(updatedUserProfile, HttpStatus.OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @DeleteMapping(path = "/{username}/delete")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username)
            throws UserNotFoundException {

        userService.deleteUser(username);
        return response(OK, USER_DELETED_SUCCESSFULLY);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }
}
