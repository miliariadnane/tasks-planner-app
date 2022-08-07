package nano.dev.tasksplanner.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import nano.dev.tasksplanner.entity.User;
import nano.dev.tasksplanner.entity.enumeration.Job;
import nano.dev.tasksplanner.entity.enumeration.Role;
import nano.dev.tasksplanner.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;

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
public class UserIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();

    @Test
    void canCreateNewUser() throws Exception {

        // Setup mocked service
        User mockUser = generateMockedUser();

        // Execute the POST request
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUser))
                );

        // Verify the response
        resultActions.andExpect(status().isCreated());
        List<User> users = userRepository.findAll();
        assertThat(users)
                .usingElementComparatorIgnoringFields("id", "userId", "password")
                .contains(mockUser);
    }

    private User generateMockedUser() {

        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        String name = String.format("%s %s",
                firstName,
                lastName
        );

        String email = String.format("%s@contact.com",
                StringUtils.trimAllWhitespace(name.trim().toLowerCase()));

        String role = faker.options().option(Role.ROLE_USER.toString(), Role.ROLE_ADMIN.toString(), Role.ROLE_SUPER_ADMIN.toString());
        Job job = faker.options().option(Job.BACKEND, Job.FRONTEND, Job.DEVOPS, Job.NETWORK, Job.SECURITY);

        User mockUser = new User(
                UUID.randomUUID().toString(),
                firstName,
                lastName,
                name,
                passwordEncoder.encode("password123"),
                email,
                new Date(),
                job,
                name,
                role,
                getRoleEnumName(role).getAuthorities(),
                true
        );

        return mockUser;
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
