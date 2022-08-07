package nano.dev.tasksplanner.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import nano.dev.tasksplanner.entity.Task;
import nano.dev.tasksplanner.entity.enumeration.Priority;
import nano.dev.tasksplanner.entity.enumeration.Status;
import nano.dev.tasksplanner.entity.enumeration.Type;
import nano.dev.tasksplanner.repository.TaskRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@Disabled
public class TaskIT {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final Faker faker = new Faker();

    @Test
    void canCreateNewTask() throws Exception {

        // Given
        Task mockTask = generateMockedTask();

        // When
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/task/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockTask))
                );

        // Then
        resultActions.andExpect(status().isCreated());
        List<Task> tasks = taskRepository.findAll();
        assertThat(tasks)
                .usingElementComparatorIgnoringFields("id", "taskId")
                .contains(mockTask);
    }

    private Task generateMockedTask() {

        String title = faker.lorem().sentence(10);
        String description = faker.lorem().paragraph();

        /* generate random priority from LOW, MEDIUM, HIGH */
        Priority priority = faker.options().option(Priority.LOW, Priority.MEDIUM, Priority.HIGH);

        /* generate random status */
        Status status = faker.options().option(Status.CLOSED, Status.NOT_STARTED, Status.IN_PROGRESS, Status.DONE);

        /* generate random type */
        Type type = faker.options().option(Type.BUG, Type.FEATURE, Type.DOCUMENTATION, Type.IMPROVEMENT, Type.TEST);

        Task mockTask = new Task(
                faker.number().randomNumber(),
                UUID.randomUUID(),
                title,
                description,
                priority,
                type,
                status,
                new Date(),
                new Date(),
                LocalDateTime.now()
        );

        return mockTask;
    }


}
