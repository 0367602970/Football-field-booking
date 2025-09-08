package vti.group10.football_booking.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vti.group10.football_booking.dto.request.BookingFieldRequest;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.BookingRepository;
import vti.group10.football_booking.repository.FootballFieldRepository;
import vti.group10.football_booking.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingFieldService {

    private final BookingRepository bookingRepository;
    private final FootballFieldRepository fieldRepository;
    private final UserRepository userRepository;

    @Transactional
    public Booking createBooking(Integer userId, BookingFieldRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FootballField field = fieldRepository.findById(request.getFieldId())
                .orElseThrow(() -> new RuntimeException("Field not found"));

        // Kiểm tra trùng lịch
        List<Booking> overlaps = bookingRepository
                .findByField_IdAndBookingDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                        field.getId(),
                        request.getBookingDate(),
                        List.of(Booking.Status.PENDING, Booking.Status.CONFIRMED),
                        request.getEndTime(),
                        request.getStartTime()
                );

        if (!overlaps.isEmpty()) {
            throw new RuntimeException("Field already booked in this time slot");
        }

        // Tính tiền
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
    public List<Booking> getBookingsByFieldAndDateAndStatus(
            Integer fieldId,
            LocalDate bookingDate,
            List<Booking.Status> statuses
    ) {
        return bookingRepository.findByField_IdAndBookingDateAndStatusIn(fieldId, bookingDate, statuses);
    }

}