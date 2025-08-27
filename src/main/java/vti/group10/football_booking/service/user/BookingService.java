package vti.group10.football_booking.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vti.group10.football_booking.dto.request.BookingRequest;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.BookingRepository;
import vti.group10.football_booking.repository.FootballFieldRepository;
import vti.group10.football_booking.repository.UserRepository;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FootballFieldRepository fieldRepository;
    private final UserRepository userRepository;

    @Transactional
    public Booking createBooking(Integer userId, BookingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FootballField field = fieldRepository.findById(request.getFieldId())
                .orElseThrow(() -> new RuntimeException("Field not found"));

        List<Booking> overlaps = bookingRepository
                .findByField_IdAndBookingDateAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        field.getId(),
                        request.getBookingDate(),
                        Booking.Status.CONFIRMED,
                        request.getEndTime(),
                        request.getStartTime()
                );

        if (!overlaps.isEmpty()) {
            throw new RuntimeException("Field already booked in this time slot");
        }

        long hours = Duration.between(request.getStartTime(), request.getEndTime()).toHours();
        if (hours <= 0) throw new RuntimeException("Invalid booking time");

        double totalPrice = field.getPricePerHour() * hours;

        Booking booking = Booking.builder()
                .user(user)
                .field(field)
                .bookingDate(request.getBookingDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .totalPrice(totalPrice)
                .status(Booking.Status.PENDING)
                .build();

        return bookingRepository.save(booking);
    }

    // ✅ Hàm huỷ booking
    @Transactional
    public Booking cancelBooking(Integer userId, Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Chỉ user tạo booking mới được huỷ
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to cancel this booking");
        }

        // Chỉ được hủy nếu booking chưa huỷ hoặc chưa hoàn thành
        if (booking.getStatus() == Booking.Status.CANCELLED) {
            throw new RuntimeException("Booking already cancelled");
        }
        if (booking.getStatus() == Booking.Status.COMPLETED) {
            throw new RuntimeException("Completed booking cannot be cancelled");
        }

        booking.setStatus(Booking.Status.CANCELLED);

        return bookingRepository.save(booking);
    }
}
