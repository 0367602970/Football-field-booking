package vti.group10.football_booking.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vti.group10.football_booking.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT SUM(b.totalPrice) FROM Booking b " +
           "WHERE b.field.id = :fieldId AND b.status = 'CONFIRMED' " +
           "AND b.bookingDate BETWEEN :startDate AND :endDate")
    Double calculateRevenue(
            @Param("fieldId") Long fieldId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(b) FROM Booking b " +
           "WHERE b.field.id = :fieldId AND b.status = 'CONFIRMED' " +
           "AND b.bookingDate BETWEEN :startDate AND :endDate")
    Long countBookings(
            @Param("fieldId") Long fieldId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
