package vti.group10.football_booking.service.owner;

import java.util.List;

import org.springframework.stereotype.Service;

import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.repository.BookingRepository;

@Service
public class BookingService {
    private final BookingRepository bookingRepo;

    public BookingService(BookingRepository bookingRepo) {
        this.bookingRepo = bookingRepo;
    }

    public List<Booking> getBookingsByOwnerAndStatus(Long ownerId, Booking.Status status) {
        return bookingRepo.findBookingsByOwnerAndStatus(ownerId, status);
    }

    public Booking updateBookingStatus(Long bookingId, Booking.Status status) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(status);
        return bookingRepo.save(booking);
    }
}
