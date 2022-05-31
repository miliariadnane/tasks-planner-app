package nano.dev.tasksplanner.repository;

import nano.dev.tasksplanner.entity.User;
import nano.dev.tasksplanner.entity.enumeration.Job;
import nano.dev.tasksplanner.entity.enumeration.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    String role_super_admin = Role.ROLE_SUPER_ADMIN.toString();
    String email = "nanodev@gmail.com";
    String username = "nanodev";

    User user = new User(
            UUID.randomUUID().toString(),
            "nano",
            "dev",
            username,
            passwordEncoder.encode("password123"),
            email,
            new Date(),
            Job.MANAGER,
            "nanodev",
            role_super_admin,
            getRoleEnumName(role_super_admin).getAuthorities(),
            true
    );

    @Test
    void itShouldSelectUserByEmailIfPresent() {
        underTest.save(user);
        // When
        User userByEmail = underTest.findUserByEmail(email);
        // Then
        assertThat(userByEmail).isNotNull();
        assertThat(userByEmail.getEmail()).isEqualTo(email);
    }

    @Test
    void willThrowExceptionIfUserNotPresentByEmail() {
        // When
        User userByEmail = underTest.findUserByEmail(email);
        // Then
        assertThat(userByEmail).isNull();
    }

    @Test
    void itShouldSelectUserByUsernameIfPresent() {
        underTest.save(user);
        // When
        User userByUsername = underTest.findUserByUsername(username);
        // then
        assertThat(userByUsername).isNotNull();
        assertThat(userByUsername.getUsername()).isEqualTo(username);
    }

    @Test
    void willThrowExceptionIfUserNotPresentByUsername() {
        // when
        User userByUsername = underTest.findUserByUsername(username);
        // then
        assertThat(userByUsername).isNull();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
