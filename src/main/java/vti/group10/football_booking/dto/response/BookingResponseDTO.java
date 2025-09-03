package vti.group10.football_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vti.group10.football_booking.model.Booking;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {
    private Integer id;

    // User info
    private Integer userId;
    private String userFullName;
    private String userEmail;

    // Field info
    private Integer fieldId;
    private String fieldName;

    // Booking info
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double totalPrice;
    private Booking.Status status;
}
