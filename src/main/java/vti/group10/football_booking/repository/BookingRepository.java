package vti.group10.football_booking.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vti.group10.football_booking.model.Booking;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(Integer userId);
    List<Booking> findByFieldId(Integer fieldId);
    List<Booking> findByStatus(Booking.Status status);
    List<Booking> findByBookingDate(LocalDate bookingDate);
    List<Booking> findByFieldIdIn(List<Integer> fieldIds);

    // Query để lấy danh sách fieldId theo ownerId
    @Query("SELECT f.id FROM FootballField f WHERE f.ownerId = :ownerId")
    List<Integer> findFieldsByOwnerId(@Param("ownerId") Integer ownerId);

    void deleteByFieldId(Integer fieldId);
    void deleteByUserId(Integer userId);
}