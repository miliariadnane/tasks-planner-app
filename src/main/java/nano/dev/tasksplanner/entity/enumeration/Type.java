package nano.dev.tasksplanner.entity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Type {

    DOCUMENTATION (5),
    TEST (4),
    IMPROVEMENT (3),
    BUG (2),
    FEATURE (1);

    private final int type;
}
