package vti.group10.football_booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type")
    private ReportType reportType;
    
    @Column(length = 255)
    private String description;
    
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt = LocalDateTime.now();
    
    @Column(name = "generated_by")
    private Integer generatedBy;
    
    public enum ReportType {
        REVENUE, USAGE, USER_ACTIVITY
    }
}