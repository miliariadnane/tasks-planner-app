package nano.dev.tasksplanner.entity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Job {

    MANAGER (6),
    SECURITY (5),
    NETWORK (4),
    DEVOPS (3),
    BACKEND (2),
    FRONTEND (1);

    private final int job;
}
