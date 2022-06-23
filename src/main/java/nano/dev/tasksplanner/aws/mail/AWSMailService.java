package nano.dev.tasksplanner.aws.mail;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AWSMailService {

    private final AmazonSimpleEmailService emailService;

    public void sendEmail(String to, String content) {
        try {
            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(new Destination().withToAddresses(to))
                    .withMessage(new Message()
                            .withBody(new Body()
                                .withHtml(new Content()
                                    .withCharset("UTF-8").withData(content))
                            )
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData("Reset Password")
                            )
                    )
                    .withSource("miliari.adnane@gmail.com");
            emailService.sendEmail(request);
        } catch (AmazonSimpleEmailServiceException e) {
            throw new IllegalStateException("Failed to send email", e);
        }
    }
}
