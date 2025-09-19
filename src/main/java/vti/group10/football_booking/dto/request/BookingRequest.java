package vti.group10.football_booking.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingRequest {
    private Integer userId;
    private Integer fieldId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
}