package vti.group10.football_booking.service.owner;

import java.util.List;

import org.springframework.stereotype.Service;

import vti.group10.football_booking.dto.BookingDTO;
import vti.group10.football_booking.dto.FieldDTO;
import vti.group10.football_booking.dto.UserDTO;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.model.FieldCluster;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.BookingRepository;

@Service
public class BookingService {
    private final BookingRepository bookingRepo;

    public BookingService(BookingRepository bookingRepo) {
        this.bookingRepo = bookingRepo;
    }
    public List<BookingDTO> getAllBookings() {
        // Lấy tất cả booking
        List<Booking> bookings = bookingRepo.findAll();

        return bookings.stream()
                // Lọc chỉ lấy booking có field và cluster visible = YES
                .filter(b -> {
                    FootballField f = b.getField();
                    return f != null
                            && f.getVisible() == FootballField.YesNo.YES
                            && f.getCluster() != null
                            && f.getCluster().getVisible() == FieldCluster.YesNo.YES;
                })
                .map(b -> {
                    User u = b.getUser();
                    UserDTO userDTO = new UserDTO(
                            u.getId(),
                            u.getEmail(),
                            u.getFullName(),
                            u.getPhone(),
                            u.getRole().name()
                    );

                    FootballField f = b.getField();
                    FieldDTO fieldDTO = f != null ? new FieldDTO(f.getId(), f.getName()) : null;

                    return new BookingDTO(
                            b.getId(),
                            userDTO,
                            fieldDTO,
                            b.getBookingDate(),
                            b.getStartTime(),
                            b.getEndTime(),
                            b.getTotalPrice(),
                            b.getStatus()
                    );
                })
                .toList();
    }

    public List<BookingDTO> getAllBookingsByOwner(Integer ownerId) {
        List<Booking> bookings = bookingRepo.findBookingsByOwner(ownerId);

        return bookings.stream().map(b -> {
            User u = b.getUser();
            UserDTO userDTO = new UserDTO(
                    u.getId(),
                    u.getEmail(),
                    u.getFullName(),
                    u.getPhone(),
                    u.getRole().name()
            );

            FootballField f = b.getField();
            FieldDTO fieldDTO = f != null ? new FieldDTO(f.getId(), f.getName()) : null;

            return new BookingDTO(
                    b.getId(),
                    userDTO,
                    fieldDTO,
                    b.getBookingDate(),
                    b.getStartTime(),
                    b.getEndTime(),
                    b.getTotalPrice(),
                    b.getStatus()
            );
        }).toList();
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

            FootballField f = b.getField();
            FieldDTO fieldDTO = f != null ? new FieldDTO(f.getId(), f.getName()) : null;

            return new BookingDTO(
                    b.getId(),
                    userDTO,
                    fieldDTO,
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

    public void deleteBooking(Integer bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        bookingRepo.delete(booking);
    }
}
