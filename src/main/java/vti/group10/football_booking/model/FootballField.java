package vti.group10.football_booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "football_fields")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FootballField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(length = 255)
    private String description;

    @Column(length = 255)
    private String name;

    @Column(name = "price_per_hour")
    private Double pricePerHour;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.AVAILABLE;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 255)
    private String city;

    @Column(nullable = false, length = 255)
    private String district;

    private Double latitude;

    private Double longitude;

    public enum Status {
        AVAILABLE, MAINTENANCE
    }
}