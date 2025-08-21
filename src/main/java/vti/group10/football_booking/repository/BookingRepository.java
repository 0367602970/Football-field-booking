package vti.group10.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vti.group10.football_booking.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
}
