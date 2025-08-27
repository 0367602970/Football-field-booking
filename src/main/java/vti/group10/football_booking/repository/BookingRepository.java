package vti.group10.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vti.group10.football_booking.model.Booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByField_IdAndBookingDateAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Integer fieldId,
            LocalDate bookingDate,
            Booking.Status status,
            LocalTime endTime,
            LocalTime startTime
    );
}
