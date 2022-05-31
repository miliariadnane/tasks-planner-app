package nano.dev.tasksplanner.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import nano.dev.tasksplanner.entity.enumeration.Job;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teamMember",uniqueConstraints = {
        @UniqueConstraint(name = "user_name_unique",columnNames = "username"),
        @UniqueConstraint(name = "user_email_unique",columnNames = "email"),
})
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode @ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable=false)
    private String userId;

    @NotNull(message="first name is required")
    @Size(min=3, message ="first name should have 3 characters minimum")
    @Column(nullable=false, length=50)
    private String firstName;

    @NotNull
    @Size(min=3, message ="last name should have 3 characters minimum")
    @Column(nullable=false, length=50)
    private String lastName;

    @NotNull(message="first name is required")
    @Column(nullable=false, length=25, unique=true)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Password is required")
    @Column(nullable=false)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message ="Please respect email format")
    @Column(nullable=false, length=120, unique=true)
    private String email;

    @Column(nullable=false)
    private Date joinDate;

    @NotNull(message = "Job field id required")
    @Enumerated
    @Column(nullable=false)
    private Job job;

    @Column(nullable=false)
    private String discordAccount;

    @Column(nullable=false)
    private String role;

    @Column(nullable=false)
    private String[] authorities;

    private boolean enable;
    private String profileImage;

    /* user can participate in many tasks */
    @JsonIgnore
    @ManyToMany(mappedBy="users", fetch = FetchType.LAZY)
    private transient Set<Task> tasks = new HashSet<>();

    public User(String userId, String firstName, String lastName, String username, String password, String email, Date joinDate, Job job, String discordAccount, String role, String[] authorities, boolean enable) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.joinDate = joinDate;
        this.job = job;
        this.discordAccount = discordAccount;
        this.role = role;
        this.authorities = authorities;
        this.enable = enable;
    }
}
