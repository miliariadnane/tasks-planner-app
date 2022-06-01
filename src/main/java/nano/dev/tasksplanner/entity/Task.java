package nano.dev.tasksplanner.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import nano.dev.tasksplanner.entity.enumeration.Priority;
import nano.dev.tasksplanner.entity.enumeration.Status;
import nano.dev.tasksplanner.entity.enumeration.Type;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;


@Entity @Table
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode
public class Task {

    @Id
    @SequenceGenerator(
            name = "task_sequence",
            sequenceName = "task_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "task_sequence",
            strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable=false)
    private UUID taskId;

    @NotNull(message = "Title of the task cannot be null")
    @NotEmpty(message = "Title of the task cannot be empty")
    @Column(nullable=false, length=80)
    private String title;

    @NotNull(message = "Details of the task cannot be null")
    @NotEmpty(message = "Details of the task cannot be empty")
    @Column(nullable=false)
    @Lob
    private String details;

    @Column(nullable=false)
    @Enumerated
    private Priority priority;

    @Column(nullable=false)
    @Enumerated
    private Type type;

    @Column(nullable=false)
    @Enumerated
    private Status status;

    @Column(nullable=false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(nullable=false)
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "America/Casablanca")
    private LocalDateTime createdAt;

    /* many tasks for many users */
    @ManyToMany
    @JoinTable(
            name = "tasks_users",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    @Transactional
    public void addUser(User user) {
        if(!getUsers().contains(user)) {
            getUsers().add(user);
        }
        if(!user.getTasks().contains(this)) {
            user.getTasks().add(this);
        }
    }

    public Task(Long id, UUID taskId, String title, String details, Priority priority, Type type, Status status, Date startDate, Date endDate, LocalDateTime createdAt) {
        this.id = id;
        this.taskId = taskId;
        this.title = title;
        this.details = details;
        this.priority = priority;
        this.type = type;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }
}
