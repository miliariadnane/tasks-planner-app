package nano.dev.tasksplanner.controller;

import nano.dev.tasksplanner.aws.AWSFileStore;
import nano.dev.tasksplanner.aws.mail.AWSMailService;
import nano.dev.tasksplanner.entity.Task;
import nano.dev.tasksplanner.entity.User;
import nano.dev.tasksplanner.entity.UserPrincipal;
import nano.dev.tasksplanner.entity.enumeration.Job;
import nano.dev.tasksplanner.entity.enumeration.Role;
import nano.dev.tasksplanner.security.jwtProvider.JWTProvider;
import nano.dev.tasksplanner.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static nano.dev.tasksplanner.security.jwtProvider.JWTProvider.TOKEN_SECRET;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    AWSFileStore awsFileStore;

    @MockBean
    AWSMailService awsMailService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTProvider jwtProvider;

    private String token;

    @Test
    @DisplayName("GET /api/v1/users/list - Success")
    void itShouldGetAllUsersSuccessfully() throws Exception {
        // Given
        String role_super_admin = Role.ROLE_SUPER_ADMIN.toString();
        String role_user = Role.ROLE_USER.toString();

        User mockUser1 = new User(
                generateUserId(),
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

        User mockUser2 = new User(
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
                getRoleEnumName(role_user).getAuthorities(),
                true
        );

        doReturn(Arrays.asList(mockUser1, mockUser2)).when(userService).getUsers();

        // Execute the GET request
        mockMvc.perform(get("/api/v1/users/list"))

                // Validate response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the response fields
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].id").value(is(1)))
                .andExpect(jsonPath("$[1].id").value(is(2)))
                .andExpect(jsonPath("$[0].role").value(is(mockUser1.getRole())))
                .andExpect(jsonPath("$[1].role").value(is(mockUser2.getRole())))
                .andExpect(jsonPath("$[0].email").value(is(mockUser1.getEmail())))
                .andExpect(jsonPath("$[1].email").value(is(mockUser2.getEmail())));
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private String generateUserId() {
        return UUID.randomUUID().toString();
    }

}
