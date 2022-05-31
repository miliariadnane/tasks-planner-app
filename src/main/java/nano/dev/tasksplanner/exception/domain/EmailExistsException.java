package nano.dev.tasksplanner.exception.domain;

public class EmailExistsException extends Exception {

    public EmailExistsException(String message) {
        super(message);
    }
}
