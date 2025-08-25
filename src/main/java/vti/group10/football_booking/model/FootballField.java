package vti.group10.football_booking.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String location;

    @Column(name = "price_per_hour")
    private Double pricePerHour;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status = Status.AVAILABLE;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "field")
    private List<Booking> bookings;

    @Builder.Default
    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FieldImage> images =new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "field")
    private List<FieldSchedule> schedules = new ArrayList<>();

    public enum Status {
        AVAILABLE, MAINTENANCE
    }

    //helper method để thêm ảnh
    public void addImage(FieldImage img) {
    images.add(img);
    img.setField(this); // tự động set ngược lại
}
}
