package vti.group10.football_booking.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vti.group10.football_booking.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // Kiểm tra booking trùng lịch
    List<Booking> findByField_IdAndBookingDateAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Integer fieldId,
            LocalDate bookingDate,
            Booking.Status status,
            LocalTime endTime,
            LocalTime startTime
    );

    // Tính tổng doanh thu theo field trong khoảng ngày
    @Query("SELECT SUM(b.totalPrice) FROM Booking b " +
            "WHERE b.field.id = :fieldId AND b.status = 'CONFIRMED' " +
            "AND b.bookingDate BETWEEN :startDate AND :endDate")
    Double calculateRevenue(
            @Param("fieldId") int fieldId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Đếm số booking theo field trong khoảng ngày
    @Query("SELECT COUNT(b) FROM Booking b " +
            "WHERE b.field.id = :fieldId AND b.status = 'CONFIRMED' " +
            "AND b.bookingDate BETWEEN :startDate AND :endDate")
    Integer countBookings(
            @Param("fieldId") int fieldId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Lấy danh sách booking theo owner và trạng thái
    @Query("SELECT b FROM Booking b WHERE b.field.owner.id = :ownerId AND b.status = :status")
    List<Booking> findBookingsByOwnerAndStatus(
            @Param("ownerId") int ownerId,
            @Param("status") Booking.Status status
    );

    // Lấy booking theo id
    Optional<Booking> findById(int bookingId);
}
