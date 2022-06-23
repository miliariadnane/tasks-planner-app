package nano.dev.tasksplanner.dbSeeder;

import nano.dev.tasksplanner.entity.Task;
import nano.dev.tasksplanner.entity.User;
import nano.dev.tasksplanner.entity.enumeration.*;
import nano.dev.tasksplanner.repository.TaskRepository;
import nano.dev.tasksplanner.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

@Configuration
public class DbSeeder {

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository,
                                        TaskRepository taskRepository,
                                        BCryptPasswordEncoder passwordEncoder)

    {
        return args -> {

            // ROLES
            String role_super_admin = Role.ROLE_SUPER_ADMIN.toString();
            String role_admin = Role.ROLE_ADMIN.toString();
            String role_user = Role.ROLE_USER.toString();

            //INSERT USERS
            User superAdmin = new User(
                    generateUserId(),
                    "nano",
                    "dev",
                    "nanodev",
                    passwordEncoder.encode("password123"),
                    "nanodev@conatct.com",
                    new Date(),
                    Job.MANAGER,
                    "nanodev",
                    role_super_admin,
                    getRoleEnumName(role_super_admin).getAuthorities(),
                    true
            );

            User admin = new User(
                    generateUserId(),
                    "admin",
                    "ADMIN",
                    "admin",
                    passwordEncoder.encode("password123"),
                    "admin@conatct.com",
                    new Date(),
                    Job.DEVOPS,
                    "admin",
                    role_admin,
                    getRoleEnumName(role_super_admin).getAuthorities(),
                    true
            );

            User user = new User(
                    generateUserId(),
                    "adnane",
                    "miliari",
                    "miliariadnane",
                    passwordEncoder.encode("password123"),
                    "miliari.adnane@gmail.com",
                    new Date(),
                    Job.BACKEND,
                    "miliariadnane",
                    role_user,
                    getRoleEnumName(role_super_admin).getAuthorities(),
                    true
            );

            userRepository.saveAll(List.of(admin,superAdmin,user));

            /*
            Task task1 = new Task(
                    1L,
                    UUID.randomUUID(),
                    "Task N°1",
                    "ras viverra lacus orci, eu semper sapien cursus ut. Praesent et magna id ex molestie aliquet pretium non dolor. Etiam fermentum mi augue, ut accumsan ipsum tincidunt pretium.",
                    Priority.LOW,
                    Type.BUG,
                    Status.IN_PROGRESS,
                    new Date(),
                    new Date(),
                    LocalDateTime.now(),
                    Set.of(user, admin)
            );

            Task task2 = new Task(
                2L,
                UUID.randomUUID(),
                "Task N°2",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                Priority.MEDIUM,
                Type.TEST,
                Status.NOT_STARTED,
                new Date(),
                new Date(),
                LocalDateTime.now(),
                Set.of(user, admin)
            );
            */

            Task task3 = new Task(
                    2L,
                    UUID.randomUUID(),
                    "Monitoring dashboard",
                    "Add a monitoring dashboard using spring actuator for http traces, tasks status and other health checks metrics.",
                    Priority.MEDIUM,
                    Type.FEATURE,
                    Status.CLOSED,
                    new Date(),
                    new Date(),
                    LocalDateTime.now(),
                    Set.of(user, admin, superAdmin)
            );

            Task task4 = new Task(
                    2L,
                    UUID.randomUUID(),
                    "Reset password",
                    "Add a reset password feature using aws simple email service.",
                    Priority.MEDIUM,
                    Type.FEATURE,
                    Status.CLOSED,
                    new Date(),
                    new Date(),
                    LocalDateTime.now(),
                    Set.of(user, admin, superAdmin)
            );

            taskRepository.saveAll(List.of(task3, task4));
//            taskRepository.saveAll(List.of(task1, task2, task3, task4));
        };

    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private String generateUserId() {
        return UUID.randomUUID().toString();
    }
}
