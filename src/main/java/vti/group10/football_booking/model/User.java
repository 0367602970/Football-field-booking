package vti.group10.football_booking.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(unique = true, length = 100)
    @Email
    private String email;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Booking> bookings;

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "generatedBy")
    private List<Report> reports;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    public enum Role {
        USER, ADMIN, OWNER
    }

    // Thêm constructor nhận id để tạo "stub entity"
    public User(Integer id) {
        this.id = id;
    }
    public enum YesNo {
        YES, NO
    }
    @Enumerated(EnumType.STRING)
    private YesNo visible = YesNo.YES;
}
