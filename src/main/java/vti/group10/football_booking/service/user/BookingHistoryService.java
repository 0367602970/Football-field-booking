package vti.group10.football_booking.service.user;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.dto.response.BookingHistoryResponse;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.BookingRepository;
import vti.group10.football_booking.repository.UserRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingHistoryService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public Page<BookingHistoryResponse> getBookingHistory(Pageable pageable) {
        // Lấy email user đang login
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Query
        Page<Booking> bookings = bookingRepository.findBookingsByUser(user.getId(), pageable);

        // Map Booking → BookingHistoryResponse
        return bookings.map(b -> {
            BookingHistoryResponse dto = new BookingHistoryResponse();
            dto.setBookingId(b.getId());
            dto.setFieldName(b.getField().getName());
            dto.setLocation(b.getField().getLocation());
            dto.setBookingDate(b.getBookingDate());
            dto.setStartTime(b.getStartTime());
            dto.setEndTime(b.getEndTime());
            dto.setTotalPrice(b.getTotalPrice());
            dto.setStatus(b.getStatus().name());
            return dto;
        });
    }
}
