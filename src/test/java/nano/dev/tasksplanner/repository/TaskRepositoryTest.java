package nano.dev.tasksplanner.repository;

import nano.dev.tasksplanner.entity.Task;
import nano.dev.tasksplanner.entity.enumeration.Priority;
import nano.dev.tasksplanner.entity.enumeration.Status;
import nano.dev.tasksplanner.entity.enumeration.Type;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class TaskRepositoryTest {

        @Autowired
        private TaskRepository underTest;

        @Test
        void itShouldSelectTasksByType() {

                // Given
                Type testType = Type.TEST;
                List<Task> tasks = List.of(
                        new Task(
                                1L,
                                UUID.randomUUID(),
                                "task day1",
                                "details task1",
                                Priority.MEDIUM,
                                testType,
                                Status.DONE,
                                new Date(),
                                new Date(),
                                LocalDateTime.now()
                        ),
                        new Task(
                                2L,
                                UUID.randomUUID(),
                                "task day2",
                                "details task2",
                                Priority.MEDIUM,
                                Type.BUG,
                                Status.IN_PROGRESS,
                                new Date(),
                                new Date(),
                                LocalDateTime.now()
                        ),
                        new Task(
                                3L,
                                UUID.randomUUID(),
                                "task day3",
                                "details task3",
                                Priority.HIGH,
                                testType,
                                Status.NOT_STARTED,
                                new Date(),
                                new Date(),
                                LocalDateTime.now()
                        )
                );

                // When
                underTest.saveAll(tasks);

                // Then
                List<Task> taskList = underTest.findByType(testType);
                List<Task> expectedTasksList = tasks.stream()
                                                        .filter(task -> task.getType().equals(testType))
                                                        .collect(Collectors.toList());
                assertThat(
                        taskList.size() == expectedTasksList.size()
                        &&
                        taskList.containsAll(expectedTasksList)
                );

        }

        @Test
        @DisplayName("Will throw exception when Task by type is invalid")
        void willThrowWhenTasksByTypeIsInvalid() {
                assertThatThrownBy(() -> underTest.findByType(Type.valueOf("NONE")))
                        .hasMessageContaining("No enum constant nano.dev.tasksplanner.entity.enumeration.Type.NONE")
                        .isInstanceOf(IllegalArgumentException.class);
        }
}
