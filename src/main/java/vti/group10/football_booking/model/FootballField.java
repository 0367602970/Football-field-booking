package vti.group10.football_booking.model;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "football_fields")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FootballField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    private String description;

    @Column(name = "price_per_hour")
    private Double pricePerHour;

    @Enumerated(EnumType.STRING)
    private Status status = Status.AVAILABLE;

    // Quan hệ với FieldCluster
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private FieldCluster cluster;

    // Optional: các booking, schedule nếu bạn cần
    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Booking> bookings;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<FieldSchedule> schedules;

    public enum Status {
        AVAILABLE, MAINTENANCE
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "visible")
    private YesNo visible = YesNo.YES;
    public enum YesNo {
        YES, NO
    }
    // Constructor nhận id
    public FootballField(Integer id) {
        this.id = id;
    }
}

