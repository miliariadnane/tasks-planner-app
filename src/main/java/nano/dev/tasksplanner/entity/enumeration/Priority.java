package nano.dev.tasksplanner.entity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Priority {

    LOW (3),
    MEDIUM (2),
    HIGH (1);

    private final int priority;
}
