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

    List<Booking> findByField_IdAndBookingDateAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Integer fieldId,
            LocalDate bookingDate,
            Booking.Status status,
            LocalTime endTime,
            LocalTime startTime
    );

    List<Booking> findByField_IdAndBookingDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            Integer fieldId,
            LocalDate bookingDate,
            List<Booking.Status> statuses,
            LocalTime endTime,
            LocalTime startTime
    );

    List<Booking> findByField_IdAndBookingDateAndStatusIn(
            Integer fieldId,
            LocalDate bookingDate,
            List<Booking.Status> statuses
    );

    // Lấy danh sách booking theo user (có thể kèm status)
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    List<Booking> findBookingsByUser(@Param("userId") int userId);

    // Phân trang
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    org.springframework.data.domain.Page<Booking> findBookingsByUser(
            @Param("userId") int userId,
            org.springframework.data.domain.Pageable pageable
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

        // Lấy danh sách booking theo owner
        @Query("SELECT b FROM Booking b WHERE b.field.owner.id = :ownerId")
        List<Booking> findBookingsByOwner(@Param("ownerId") int ownerId);
    
}
