package nano.dev.tasksplanner.entity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {

    NOT_STARTED (4),
    IN_PROGRESS (3),
    DONE (2),
    CLOSED (1);

    private final int status;
}
