package vti.group10.football_booking.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduleRequest {
    @NotNull 
    private LocalDate availableDate;

    @NotNull 
    private LocalTime startTime;

    @NotNull 
    private LocalTime endTime;
}
