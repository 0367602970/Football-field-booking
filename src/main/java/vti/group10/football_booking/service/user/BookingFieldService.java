package vti.group10.football_booking.service.user;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.request.BookingFieldRequest;
import vti.group10.football_booking.dto.response.BookingResponseDTO;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.BookingRepository;
import vti.group10.football_booking.repository.FootballFieldRepository;

@Service
@RequiredArgsConstructor
public class BookingFieldService {

    private final BookingRepository bookingRepository;
    private final FootballFieldRepository fieldRepository;

    // ✅ Tạo booking
    public Booking createBooking(Integer userId, BookingFieldRequest request) {
        FootballField field = fieldRepository.findById(request.getFieldId())
                .orElseThrow(() -> new RuntimeException("Field not found"));

        double hours = (request.getEndTime().toSecondOfDay() - request.getStartTime().toSecondOfDay()) / 3600.0;

        Booking booking = Booking.builder()
                .field(field)
                .user(User.builder().id(userId).build())
                .bookingDate(request.getBookingDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .totalPrice(field.getPricePerHour() * hours)
                .status(Booking.Status.PENDING)
                .build();

        return bookingRepository.save(booking);
    }

    // ✅ Hủy booking trả về đối tượng Booking (để controller map DTO)
    public Booking cancelBooking(int userId, int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to cancel this booking");
        }

        booking.setStatus(Booking.Status.CANCELLED);
        return bookingRepository.save(booking);
    }

    // ✅ Lấy danh sách booking theo user
    public List<BookingResponseDTO> listUserBookings(int userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::mapBookingToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Lấy danh sách booking theo field + date + status
    public List<BookingResponseDTO> getBookingsByFieldAndDateAndStatus(
            Integer fieldId,
            LocalDate bookingDate,
            List<Booking.Status> statuses
    ) {
        return bookingRepository.findByField_IdAndBookingDateAndStatusIn(fieldId, bookingDate, statuses)
                .stream()
                .map(this::mapBookingToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Map entity sang DTO
    private BookingResponseDTO mapBookingToResponse(Booking booking) {
        FootballField field = booking.getField();
        String clusterName = (field.getCluster() != null) ? field.getCluster().getName() : null;

        return BookingResponseDTO.builder()
                .id(booking.getId())
                .fieldId(field.getId())
                .fieldName(field.getName())
                .clusterName(clusterName)
                .userId(booking.getUser().getId())
                .userFullName(booking.getUser().getFullName())
                .userEmail(booking.getUser().getEmail())
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .build();
    }
}
