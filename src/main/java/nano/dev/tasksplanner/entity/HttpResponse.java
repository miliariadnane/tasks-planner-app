package nano.dev.tasksplanner.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collection;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static nano.dev.tasksplanner.util.DateUtil.dateTimeFormatter;

@Data
@SuperBuilder
@JsonInclude(NON_NULL)
public class HttpResponse<T> {
    protected String message;
    protected HttpStatus status;
    protected int statusCode;
    protected String reason;
    protected String timeStamp;
    protected Collection<? extends T> tasks;

    public HttpResponse(int statusCode, HttpStatus status, String reason, String message) {
        this.timeStamp = LocalDateTime.now().format(dateTimeFormatter());
        this.statusCode = statusCode;
        this.status = status;
        this.reason = reason;
        this.message = message;
    }
}
