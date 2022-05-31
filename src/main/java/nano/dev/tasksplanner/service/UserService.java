package nano.dev.tasksplanner.service;

import nano.dev.tasksplanner.entity.User;
import nano.dev.tasksplanner.exception.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface UserService {

    User addNewUser(User user)
            throws UserNotFoundException, UsernameExistException, EmailExistsException;

    User updateUser(User user)
            throws UserNotFoundException, UsernameExistException, EmailExistsException;

    User updateUserProfile(String currentUsername, String firstName, String lastName, String username, String email, String role, boolean enable, String job, String discordAccount)
            throws UserNotFoundException, UsernameExistException, EmailExistsException;

    List<User> getUsers();

    void deleteUser(String username) throws UserNotFoundException;

    User findUserByUsername(String username);

    User findUserByEmail(String email);
}
