package nano.dev.tasksplanner.service.Impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.dev.tasksplanner.aws.AWSBucket;
import nano.dev.tasksplanner.aws.AWSFileStore;
import nano.dev.tasksplanner.entity.User;
import nano.dev.tasksplanner.entity.UserPrincipal;
import nano.dev.tasksplanner.entity.enumeration.Job;
import nano.dev.tasksplanner.entity.enumeration.Role;
import nano.dev.tasksplanner.exception.domain.*;
import nano.dev.tasksplanner.repository.UserRepository;
import nano.dev.tasksplanner.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static nano.dev.tasksplanner.service.Impl.constant.UserImplConstant.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Transactional
@Qualifier("userDetailsService")
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private final AWSFileStore awsFileStore;

    @Override
    public List<User> getUsers() {
        log.info("Fetching all the users from the database");
        return userRepository.findAll();
    }

    @Override
    public User addNewUser(User user)
            throws UserNotFoundException, UsernameExistException, EmailExistsException {

        log.info("validate email and username of the new user - creation");
        validateNewUsernameAndEmail(EMPTY, user.getUsername(), user.getEmail());

        user.setUserId(generateUserId());
        user.setJoinDate(new Date());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(getRoleEnumName(user.getRole()).name());
        user.setAuthorities(getRoleEnumName(user.getRole()).getAuthorities());

        userRepository.save(user);
        log.info("user added successfully");

        return user;
    }

    @Override
    public User updateUser(User user)
            throws UserNotFoundException, UsernameExistException, EmailExistsException {

        log.info("validate email and username of the new user - update");
        User updatedUser = validateNewUsernameAndEmail(
                user.getUsername(),
                user.getUsername(),
                user.getEmail()
        );

        updatedUser.setFirstName(user.getFirstName());
        updatedUser.setLastName(user.getLastName());
        updatedUser.setUsername(user.getUsername());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setEnable(user.isEnable());
        updatedUser.setRole(getRoleEnumName(user.getRole()).name());
        updatedUser.setAuthorities(getRoleEnumName(user.getRole()).getAuthorities());
        updatedUser.setJob(user.getJob());
        updatedUser.setDiscordAccount(user.getDiscordAccount());

        userRepository.save(updatedUser);
        log.info("user updated successfully");

        return updatedUser;
    }

    @Override
    public User updateUserProfile(String currentUsername, String firstName, String lastName, String username, String email, String role, boolean enable, String job, String discordAccount)
            throws UserNotFoundException, UsernameExistException, EmailExistsException {

        log.info("validate email and username of the new user - update profile");
        User updatedUser = validateNewUsernameAndEmail(currentUsername,username,email);

        updatedUser.setFirstName(firstName);
        updatedUser.setLastName(lastName);
        updatedUser.setUsername(username);
        updatedUser.setEmail(email);
        updatedUser.setEnable(enable);
        updatedUser.setRole(getRoleEnumName(role).name());
        updatedUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        updatedUser.setJob(Job.valueOf(job));
        updatedUser.setDiscordAccount(discordAccount);

        userRepository.save(updatedUser);
        log.info("user updated successfully");

        return updatedUser;
    }

    @Override
    public void deleteUser(String username) throws UserNotFoundException {
        // Check the Course exists
        User user = userRepository.findUserByUsername(username);
        if(user == null) {
            throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + user.getUsername());
        }

        // delete user image
        if(user.getProfileImage() != null) {
            String bucket = String.format("%s", AWSBucket.USER_IMAGE.getBucketName());
            String key = String.format("user/userId-%d",user.getId());
            awsFileStore.delete(bucket,key);
            log.info("user image deleted from bucket successfully");
        }

        // delete image
        userRepository.deleteById(user.getId());
        log.info("user deleted from DB successfully");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            log.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        }

        userRepository.save(user);
        UserPrincipal userPrincipal = new UserPrincipal(user);
        log.info(FOUND_USER_BY_USERNAME + username);
        return userPrincipal;
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    private String generateUserId() {
        return UUID.randomUUID().toString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
            throws UserNotFoundException, UsernameExistException, EmailExistsException {

        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
        if(StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if(currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if(userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                // second condition verify the update case
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if(userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }
}
