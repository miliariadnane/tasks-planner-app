package nano.dev.tasksplanner.controller;

import lombok.AllArgsConstructor;
import nano.dev.tasksplanner.entity.HttpResponse;
import nano.dev.tasksplanner.exception.domain.UserNotFoundException;
import nano.dev.tasksplanner.service.ResetPasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "api/v1/reset-password")
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;
    public static final String EMAIL_SENT = "An email with a new password was sent to: ";

    public ResetPasswordController(ResetPasswordService resetPasswordService) {
        this.resetPasswordService = resetPasswordService;
    }

    @GetMapping("/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)
            throws UserNotFoundException
    {
        resetPasswordService.resetPassword(email);
        return response(OK, EMAIL_SENT + email);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }
}
