package vti.group10.football_booking.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.payment WHERE b.status = :status AND b.createdAt < :time")
    List<Booking> findPendingBookingsWithPayment(@Param("status") Booking.Status status,
                                                 @Param("time") LocalDateTime time);
    List<Booking> findByStatusAndCreatedAtBefore(Booking.Status status, LocalDateTime time);
    // Lấy booking theo userId
    List<Booking> findByUserId(Integer userId);

    // Kiểm tra trùng lịch (1 trạng thái)
    List<Booking> findByField_IdAndBookingDateAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Integer fieldId,
            LocalDate bookingDate,
            Booking.Status status,
            LocalTime endTime,
            LocalTime startTime
    );

    // Kiểm tra trùng lịch (nhiều trạng thái)
    List<Booking> findByField_IdAndBookingDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            Integer fieldId,
            LocalDate bookingDate,
            List<Booking.Status> statuses,
            LocalTime endTime,
            LocalTime startTime
    );

    // Lấy danh sách booking theo field + date + status
    List<Booking> findByField_IdAndBookingDateAndStatusIn(
            Integer fieldId,
            LocalDate bookingDate,
            List<Booking.Status> statuses
    );
    Optional<Booking> findByPaymentToken(String paymentToken);

    // Lấy danh sách booking theo user (không phân trang)
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    List<Booking> findBookingsByUser(@Param("userId") int userId);

    // Lấy danh sách booking theo user (có phân trang)
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.id DESC")
    org.springframework.data.domain.Page<Booking> findBookingsByUser(
            @Param("userId") int userId,
            org.springframework.data.domain.Pageable pageable
    );

    // Tính tổng doanh thu theo field
    @Query("SELECT SUM(b.totalPrice) FROM Booking b " +
            "WHERE b.field.id = :fieldId AND b.status = 'CONFIRMED' " +
            "AND b.bookingDate BETWEEN :startDate AND :endDate")
    Double calculateRevenue(
            @Param("fieldId") int fieldId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Đếm số booking theo field
    @Query("SELECT COUNT(b) FROM Booking b " +
            "WHERE b.field.id = :fieldId AND b.status = 'CONFIRMED' " +
            "AND b.bookingDate BETWEEN :startDate AND :endDate")
    Integer countBookings(
            @Param("fieldId") int fieldId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Lấy booking theo owner và status (sửa theo cluster.owner)
    @Query("SELECT b FROM Booking b WHERE b.field.cluster.owner.id = :ownerId AND b.status = :status")
    List<Booking> findBookingsByOwnerAndStatus(
            @Param("ownerId") int ownerId,
            @Param("status") Booking.Status status
    );

    // Lấy booking theo owner (tất cả)
    @Query("""
    SELECT b FROM Booking b 
    WHERE b.field.visible = vti.group10.football_booking.model.FootballField.YesNo.YES
      AND b.field.cluster.visible = vti.group10.football_booking.model.FieldCluster.YesNo.YES
      AND b.field.cluster.owner.id = :ownerId
    """)
    List<Booking> findBookingsByOwner(@Param("ownerId") int ownerId);
}
