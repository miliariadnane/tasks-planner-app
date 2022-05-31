package nano.dev.tasksplanner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nano.dev.tasksplanner.aws.AWSFileStore;
import nano.dev.tasksplanner.entity.HttpResponse;
import nano.dev.tasksplanner.entity.Task;
import nano.dev.tasksplanner.entity.enumeration.Priority;
import nano.dev.tasksplanner.entity.enumeration.Status;
import nano.dev.tasksplanner.entity.enumeration.Type;
import nano.dev.tasksplanner.service.TaskService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.*;

import static nano.dev.tasksplanner.util.DateUtil.dateTimeFormatter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Disabled
class TaskControllerTest {

    @MockBean
    private TaskService taskService;

    @MockBean
    private AWSFileStore awsFileStore;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/task/1 - Success")
    void itShouldGetTaskByIdSuccessfully() throws Exception {

        // Given
        Task mockTask = new Task(
                1L,
                UUID.randomUUID(),
                "Task day1",
                "Details task day1",
                Priority.LOW,
                Type.BUG,
                Status.CLOSED,
                new Date(),
                new Date(),
                LocalDateTime.now()
        );
        given(taskService.getTask(1L)).willReturn(Optional.of(mockTask));

        // When
        // Then
        mockMvc.perform(get("/api/v1/task/{id}", 1L))

                // Validate response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

                // Validate the returned fields
                assertThat(taskService.getTask(1L))
                    .isPresent()
                    .hasValueSatisfying(t ->
                            assertThat(t).usingRecursiveComparison().isEqualTo(mockTask));
    }

    @Test
    @DisplayName("GET /api/v1/task/all - Success")
    void itShouldGetAllTasksSuccessfully() throws Exception {

        // Given
        Task mockTask1 = new Task(
                1L,
                UUID.randomUUID(),
                "Task day1",
                "Details task day1",
                Priority.LOW,
                Type.BUG,
                Status.CLOSED,
                new Date(),
                new Date(),
                LocalDateTime.now()
        );
        Task mockTask2 = new Task(
                2L,
                UUID.randomUUID(),
                "Task day2",
                "Details task day2",
                Priority.MEDIUM,
                Type.FEATURE,
                Status.DONE,
                new Date(),
                new Date(),
                LocalDateTime.now()
        );

        HttpResponse<Task> mockResponse = HttpResponse.<Task>builder()
                .tasks(Arrays.asList(mockTask1, mockTask2))
                .message("Task created successfully")
                .status(OK)
                .statusCode(OK.value())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build();
        given(taskService.getTasks()).willReturn(mockResponse);

        // When
        // Then
        mockMvc.perform(get("/api/v1/task/all"))

                // Validate response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned object
                .andExpect(jsonPath("$.tasks", hasSize(2)));

                assertIterableEquals(taskService.getTasks().getTasks(), mockResponse.getTasks());
    }

    @Test
    @DisplayName("GET /api/v1/task/1 - Not Found")
    @Disabled
    void itShouldThrowWhenGetTaskNotFound() throws Exception {

        // Given
        given(taskService.getTask(1L)).willReturn(Optional.empty());

        // When
        // Then
        mockMvc.perform(get("/api/v1/task/{id}", 99L))
                .andExpect(status().isNotFound());

        // ... no interaction with taskService
        then(taskService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("POST /api/v1/task/add - Success")
    @Disabled
    void itShouldCreateTaskSuccessfully() throws Exception {

        // Given
        Task mockTask = new Task(
                1L,
                UUID.randomUUID(),
                "Task day1",
                "Details task day1",
                Priority.LOW,
                Type.BUG,
                Status.CLOSED,
                new Date(),
                new Date(),
                LocalDateTime.now()
        );

        Task postTask = new Task(
                1L,
                UUID.randomUUID(),
                "Task day1",
                "Details task day1",
                Priority.LOW,
                Type.BUG,
                Status.CLOSED,
                new Date(),
                new Date(),
                LocalDateTime.now()
        );

        HttpResponse<Task> mockResponse = HttpResponse.<Task>builder()
                .tasks(Collections.singletonList(mockTask))
                .message("Task created successfully")
                .status(OK)
                .statusCode(OK.value())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build();

        given(taskService.saveTask(any())).willReturn(mockResponse);

        // When
        // Then
        ResultActions resultActions = mockMvc.perform(post("/api/v1/task/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(asJsonString(postTask))));

            // Validate the response code and content type
            resultActions.andExpect(status().isOk());
            resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

            // Validate the returned fields
            .andExpect(jsonPath("$.id", is(1L)))
            .andExpect(jsonPath("$.uuid", is(mockTask.getTaskId().toString())))
            .andExpect(jsonPath("$.title", is(mockTask.getTitle())))
            .andExpect(jsonPath("$.details", is(mockTask.getDetails())))
            .andExpect(jsonPath("$.priority", is(mockTask.getPriority().toString())))
            .andExpect(jsonPath("$.type", is(mockTask.getType().toString())))
            .andExpect(jsonPath("$.status", is(mockTask.getStatus().toString())))
            .andExpect(jsonPath("$.createdAt", is(LocalDateTime.now().format(dateTimeFormatter()))));
    }

    @Test
    @DisplayName("DELETE /api/v1/task/delete/1 - Success")
    void itShouldDeleteTaskSuccessfully() throws Exception {

        // Given
        Task mockTask = new Task(
                1L,
                UUID.randomUUID(),
                "Task day1",
                "Details task day1",
                Priority.LOW,
                Type.BUG,
                Status.CLOSED,
                new Date(),
                new Date(),
                LocalDateTime.now()
        );

        HttpResponse<Task> mockResponse = HttpResponse.<Task>builder()
                .tasks(Collections.singletonList(mockTask))
                .message("Task deleted successfully")
                .status(OK)
                .statusCode(OK.value())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build();

        given(taskService.getTask(1L)).willReturn(Optional.of(mockTask));
        given(taskService.deleteTask(1L)).willReturn(mockResponse);

        // When
        // Then
        mockMvc.perform(delete("/api/v1/task/delete/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/task/delete/1 - Not Found")
    @Disabled
    void itShouldThrowWhenDeleteNotFound() throws Exception {

        // Given
        HttpResponse<Task> retrunedTask = HttpResponse.<Task>builder()
                .tasks(Collections.emptyList())
                .message("Task not found")
                .status(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build();

        given(taskService.deleteTask(1L)).willReturn(retrunedTask);
        // When
        // Then
        mockMvc.perform(delete("/api/v1/task/delete/{id}", 1L))
                .andExpect(status().isNotFound());



    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
