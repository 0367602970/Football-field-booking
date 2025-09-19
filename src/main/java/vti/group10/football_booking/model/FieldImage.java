package vti.group10.football_booking.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "field_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private FieldCluster cluster;
}
