package nano.dev.tasksplanner.service;

import nano.dev.tasksplanner.entity.HttpResponse;
import nano.dev.tasksplanner.entity.Task;
import nano.dev.tasksplanner.entity.User;
import nano.dev.tasksplanner.entity.enumeration.*;
import nano.dev.tasksplanner.exception.domain.TaskNotFoundException;
import nano.dev.tasksplanner.repository.TaskRepository;
import nano.dev.tasksplanner.repository.UserRepository;
import nano.dev.tasksplanner.service.Impl.TaskServiceImpl;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private WebhookService webhookService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private TaskService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new TaskServiceImpl(taskRepository, webhookService);
    }

    @Test
    @DisplayName("Should Create Task")
    void itShouldCreateTaskSuccessfully() throws TaskNotFoundException {
        // Given
        Type testType = Type.TEST;

        String role_super_admin = Role.ROLE_SUPER_ADMIN.toString();
        User user = new User(
                UUID.randomUUID().toString(),
                "john",
                "doe",
                "johndoe",
                passwordEncoder.encode("password123"),
                "johndoe@conatct.com",
                new Date(),
                Job.MANAGER,
                "nanodev",
                role_super_admin,
                getRoleEnumName(role_super_admin).getAuthorities(),
                true
        );
        
        Task task = new Task(
                1L,
                UUID.randomUUID(),
                "Task day1",
                "Details task day1",
                Priority.LOW,
                testType,
                Status.CLOSED,
                new Date(),
                new Date(),
                LocalDateTime.now(),
                Set.of(user)
        );
                
        // When
        underTest.saveTask(task);
        
        // Then
        ArgumentCaptor<Task> taskArgumentCaptor =
                ArgumentCaptor.forClass(Task.class);
        
        then(taskRepository).should().saveAndFlush(taskArgumentCaptor.capture());

        Task taskArgumentCaptorValue = taskArgumentCaptor.getValue();
        assertThat(taskArgumentCaptorValue)
                .isEqualTo(task);
    }

    @Test
    @DisplayName("Should Return All Tasks")
    void itShouldReturnTaskList() {

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
                Priority.HIGH,
                Type.BUG,
                Status.DONE,
                new Date(),
                new Date(),
                LocalDateTime.now()
        );
        doReturn(Arrays.asList(mockTask1, mockTask2)).when(taskRepository).findAll();

        // When
        HttpResponse<Task> taskList = underTest.getTasks();

        // Then
        assertEquals(2, taskList.getTasks().size());
    }

    @Test
    @DisplayName("It should Return Task When findById Success")
    void itShouldReturnTaskWhenFindByIdSuccess() throws TaskNotFoundException {

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

        given(taskRepository.findById(1L)).willReturn(Optional.of(mockTask));

        // When
        Optional<Task> task = underTest.getTask(1L);

        // Then
        assertTrue(task.isPresent(), "Task should be present");
        assertSame(task.get(), mockTask, "Tasks should be the same");
    }

    @Test
    @DisplayName("Will throw  Exception When Task Not Found")
    void willThrowWhenTaskNotFound() {

        // Given
        long id = 1L;
        given(taskRepository.findById(id)).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getTask(id))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("The task was not found on the server");
    }

    @Test
    @DisplayName("Can delete task")
    void canDeleteTask() throws TaskNotFoundException {
        // Given
        long id = 1L;
        Task mockTask1 = new Task(
                id,
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
        given(taskRepository.findById(id)).willReturn(Optional.of(mockTask1));

        // When
        underTest.deleteTask(id);

        // Then
        verify(taskRepository, times(1)).delete(mockTask1);
    }

    @Test
    @DisplayName("Will throw exception when delete task not found")
    void willThrowWhenDeleteTaskNotFound() {
        // Given
        long id = 10L;
        given(taskRepository.findById(id)).willReturn(Optional.empty());

        // When
        // Then
        AssertionsForClassTypes.assertThatThrownBy(() -> underTest.deleteTask(id))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("The task was not found on the server");

        verify(taskRepository, never()).deleteById(any());
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
