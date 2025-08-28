package vti.group10.football_booking.service.owner;

import java.util.List;

import org.springframework.stereotype.Service;

import vti.group10.football_booking.dto.BookingDTO;
import vti.group10.football_booking.dto.UserDTO;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.BookingRepository;

@Service
public class BookingService {
    private final BookingRepository bookingRepo;

    public BookingService(BookingRepository bookingRepo) {
        this.bookingRepo = bookingRepo;
    }

    public List<BookingDTO> getBookingsByOwnerAndStatus(Integer ownerId, Booking.Status status) {
        List<Booking> bookings = bookingRepo.findBookingsByOwnerAndStatus(ownerId, status);

        return bookings.stream().map(b -> {
            User u = b.getUser();
            UserDTO userDTO = new UserDTO(
                    u.getId(),
                    u.getEmail(),
                    u.getFullName(),
                    u.getPhone(),
                    u.getRole().name()
            );
            return new BookingDTO(
                    b.getId(),
                    userDTO,
                    b.getBookingDate(),
                    b.getStartTime(),
                    b.getEndTime(),
                    b.getTotalPrice(),
                    b.getStatus()
            );
        }).toList();
    }

    public Booking updateBookingStatus(Integer bookingId, Booking.Status status) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(status);
        return bookingRepo.save(booking);
    }
}
