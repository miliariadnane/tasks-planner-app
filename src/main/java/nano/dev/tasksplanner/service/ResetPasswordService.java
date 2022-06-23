package nano.dev.tasksplanner.service;

import nano.dev.tasksplanner.exception.domain.UserNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public interface ResetPasswordService {

    String buildEmail(String name, String link);
    void resetPassword(String email) throws UserNotFoundException;
}
