package nano.dev.tasksplanner.service.Impl;

import lombok.extern.slf4j.Slf4j;
import nano.dev.tasksplanner.entity.Task;
import nano.dev.tasksplanner.entity.User;
import nano.dev.tasksplanner.exception.domain.TaskNotFoundException;
import nano.dev.tasksplanner.exception.domain.UserNotFoundException;
import nano.dev.tasksplanner.repository.TaskRepository;
import nano.dev.tasksplanner.repository.UserRepository;
import nano.dev.tasksplanner.service.DetachUserService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DetachUserServiceImpl implements DetachUserService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public DetachUserServiceImpl(TaskRepository taskRepository,
                                 UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean detachUserFromTask(long taskId, long userId)
            throws TaskNotFoundException, UserNotFoundException {

        Task task = taskRepository.findByTaskId(taskId);
        if(task == null) throw new TaskNotFoundException("The task was not found on the server");

        User user = userRepository.findUserById(userId);
        if(user == null) throw new UserNotFoundException("User was not found on the server");

        if(task.getUsers().contains(user)) {
            task.getUsers().remove(user);
            taskRepository.save(task);
            log.info("user detached from the task successfully");
            return true;
        }

        return false;
    }
}
