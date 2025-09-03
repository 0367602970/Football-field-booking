package vti.group10.football_booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "field_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "available_date")
    private LocalDate availableDate;
    
    @Column(name = "end_time")
    private LocalTime endTime;
    
    @Column(name = "is_booked")
    private Boolean isBooked = false;
    
    @Column(name = "start_time")
    private LocalTime startTime;
    
    @Column(name = "field_id")
    private Integer fieldId;
}