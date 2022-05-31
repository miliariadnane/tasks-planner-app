package nano.dev.tasksplanner.service;

import nano.dev.tasksplanner.aws.AWSFileStore;
import nano.dev.tasksplanner.entity.User;
import nano.dev.tasksplanner.entity.enumeration.Job;
import nano.dev.tasksplanner.entity.enumeration.Role;
import nano.dev.tasksplanner.exception.domain.EmailExistsException;
import nano.dev.tasksplanner.exception.domain.UserNotFoundException;
import nano.dev.tasksplanner.exception.domain.UsernameExistException;
import nano.dev.tasksplanner.repository.UserRepository;
import nano.dev.tasksplanner.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AWSFileStore awsFileStore;

    private UserServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserServiceImpl(userRepository, passwordEncoder, awsFileStore);
    }

    @Test
    void canGetAllUsers() {
        // when
        underTest.getUsers();
        // then
        verify(userRepository).findAll();
    }

    @Test
    void canLoadUserByUsername() {
        // given
        String username = "miliariadnane";
        given(userRepository.findUserByUsername(username)).willReturn(new User());
        // when
        underTest.loadUserByUsername(username);
        // then
        verify(userRepository).findUserByUsername(username);
    }

    @Test
    void canAddUser() throws UserNotFoundException, EmailExistsException, UsernameExistException {
        // given
        String role_super_admin = Role.ROLE_SUPER_ADMIN.toString();
        User user = new User(
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

        // when
        underTest.addNewUser(user);

        // then
        ArgumentCaptor<User> studentArgumentCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(studentArgumentCaptor.capture());
        User capturedUser = studentArgumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(user);
    }

    @Test
    void canUpdateUser() {
        // given
        // when
        // then
    }

    @Test
    void canDeleteUser() throws UserNotFoundException {
        // given
        String username = "miliariadnane";
        User user = new User();
        user.setUsername(username);
        given(userRepository.findUserByUsername(username)).willReturn(user);

        // when
        underTest.deleteUser(username);

        // then
        verify(userRepository).deleteById(user.getId());
    }

    @Test
    @Disabled
    void willThrowExceptionWhenDeleteUserNotFound() {
        // given
        String username = "miliariadnane";
        given(userRepository.findUserByUsername(username)).willReturn(null);

        // when
        assertThatThrownBy(() -> underTest.deleteUser(username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("No user found by username: " + username);

        // then
        verify(userRepository, never()).deleteById(any());
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private String generateUserId() {
        return UUID.randomUUID().toString();
    }
}
