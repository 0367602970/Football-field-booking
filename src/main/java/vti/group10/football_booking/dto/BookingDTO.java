package vti.group10.football_booking.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vti.group10.football_booking.model.Booking;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private Integer id;
    private UserDTO user;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double totalPrice;
    private Booking.Status status;
}
