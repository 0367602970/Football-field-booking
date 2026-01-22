package vti.group10.football_booking.dto.request;

import lombok.Data;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.model.User;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingFieldRequest {
    private Integer userId;
    private Integer fieldId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;

    // 👉 Convert sang Booking entity
    public Booking toBooking(Integer userId) {
        double hours = (endTime.toSecondOfDay() - startTime.toSecondOfDay()) / 3600.0;

        return Booking.builder()
                .field(new FootballField(fieldId))   // chỉ gán id field
                .user(new User(userId))              // chỉ gán id user
                .bookingDate(bookingDate)
                .startTime(startTime)
                .endTime(endTime)
                .totalPrice(0.0) // tính sau ở service
                .status(Booking.Status.PENDING)
                .build();
    }
}
