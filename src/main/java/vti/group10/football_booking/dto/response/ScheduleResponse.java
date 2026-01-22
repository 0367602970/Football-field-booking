package vti.group10.football_booking.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleResponse {
    private Integer id;
    private LocalDate availableDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isBooked;
}
