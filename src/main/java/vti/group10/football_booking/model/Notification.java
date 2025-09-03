package vti.group10.football_booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(length = 255)
    private String message;
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.UNREAD;
    
    @Column(name = "user_id")
    private Integer userId;
    
    public enum Status {
        READ, UNREAD
    }
}