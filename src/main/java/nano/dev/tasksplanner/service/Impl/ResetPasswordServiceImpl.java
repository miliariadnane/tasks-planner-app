package nano.dev.tasksplanner.service.Impl;

import lombok.RequiredArgsConstructor;
import nano.dev.tasksplanner.aws.mail.AWSMailService;
import nano.dev.tasksplanner.entity.User;
import nano.dev.tasksplanner.exception.domain.UserNotFoundException;
import nano.dev.tasksplanner.repository.UserRepository;
import nano.dev.tasksplanner.service.ResetPasswordService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static nano.dev.tasksplanner.service.Impl.constant.UserImplConstant.NO_USER_FOUND_BY_EMAIL;
import static nano.dev.tasksplanner.service.Impl.constant.UserImplConstant.NO_USER_FOUND_BY_USERNAME;

@Service
@RequiredArgsConstructor
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private final AWSMailService awsMailService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public String buildEmail(String name, String password) {
        return """
        <body>
            <p> Hi %s </p>
            <p>You have requested to reset your password.</p>
            <p>Your new account password is: %s</p>
            <p>The support team</a></p>
            <p>See you soon !</p>
        </body>
        """.formatted(name, password);
    }

    @Override
    @Transactional
    public void resetPassword(String email) throws UserNotFoundException {

        User user = userRepository.findUserByEmail(email);
        if(user == null) {
            throw new UserNotFoundException(NO_USER_FOUND_BY_EMAIL + user.getEmail());
        }
        String password = generatePassword();
        user.setPassword(bCryptPasswordEncoder.encode(password));
        String link = "http://tasksplanner-env.eba-pkikrhha.us-east-1.elasticbeanstalk.com/api/v1/reset-password";
        awsMailService.sendEmail(user.getEmail(), buildEmail(user.getFirstName(), password));
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
