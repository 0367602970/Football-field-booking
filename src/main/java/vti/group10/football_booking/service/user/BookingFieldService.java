package vti.group10.football_booking.service.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import vti.group10.football_booking.dto.request.BookingFieldRequest;
import vti.group10.football_booking.dto.request.SepayWebhookRequest;
import vti.group10.football_booking.dto.response.BookingResponseDTO;
import vti.group10.football_booking.model.*;
import vti.group10.football_booking.repository.*;

@Service
@RequiredArgsConstructor
public class BookingFieldService {

    private final BookingRepository bookingRepository;
    private final FootballFieldRepository fieldRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    public Booking getBookingById(Integer bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking not found with id: " + bookingId));
    }
    // ✅ Tạo booking
    public Booking createBooking(Integer userId, BookingFieldRequest request) {
        FootballField field = fieldRepository.findById(request.getFieldId())
                .orElseThrow(() -> new RuntimeException("Field not found"));

        double hours = (request.getEndTime().toSecondOfDay() - request.getStartTime().toSecondOfDay()) / 3600.0;
        // Tạo UUID
        String rawToken = UUID.randomUUID().toString();

        // Chuẩn hoá: bỏ "-" và viết hoa
        String normalizedToken = rawToken.replace("-", "").toUpperCase();
        Booking booking = Booking.builder()
                .field(field)
                .user(User.builder().id(userId).build())
                .bookingDate(request.getBookingDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .totalPrice(field.getPricePerHour() * hours)
                .paymentToken(normalizedToken)
                .status(Booking.Status.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // Tạo payment liên quan
        Payment payment = Payment.builder()
                .booking(savedBooking)
                .amount(savedBooking.getTotalPrice())
                .paymentMethod(Payment.Method.BANK) // default hoặc theo request
                .status(Payment.Status.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        User customer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User owner = field.getCluster().getOwner();

        String message = String.format(
                "Khách hàng %s đã đặt %s khung giờ %s - %s ngày %s với giá trị hóa đơn %.0f VND",
                customer.getFullName(),
                field.getName(),
                request.getStartTime(),
                request.getEndTime(),
                request.getBookingDate(),
                savedBooking.getTotalPrice()
        );

        Notification notification = Notification.builder()
                .user(owner) // chủ sân nhận thông báo
                .message(message)
                .status(Notification.Status.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        return savedBooking;
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

    @Transactional
    public void handleSepayWebhook(SepayWebhookRequest payload) {
        // Chỉ xử lý khi là giao dịch tiền vào
        if (!"in".equalsIgnoreCase(payload.getTransferType())) {
            return;
        }

        String content = payload.getContent();
        if (content == null) {
            return;
        }

        // Tìm token sau "SEVQR "
        String token = null;
        Pattern pattern = Pattern.compile("SEVQR\\s+([A-Za-z0-9\\-]+)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            token = matcher.group(1); // lấy đúng paymentToken
        }

        if (token == null) {
            throw new RuntimeException("Payment token not found in content: " + content);
        }

        // Tìm booking theo token
        String finalToken = token;
        Booking booking = bookingRepository.findByPaymentToken(finalToken)
                .orElseThrow(() -> new RuntimeException("Booking not found for token: " + finalToken));

        // Xác minh số tiền khớp
        if (!payload.getTransferAmount().equals(booking.getTotalPrice())) {
            throw new RuntimeException("Invalid amount for token: " + token);
        }

        // Cập nhật trạng thái
        booking.setStatus(Booking.Status.CONFIRMED);
        bookingRepository.save(booking);

        Payment payment = paymentRepository.findByBookingId(booking.getId())
                .orElseThrow(() -> new RuntimeException("Payment not found for booking: " + booking.getId()));
        payment.setStatus(Payment.Status.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        User customer = booking.getUser();
        FootballField field = booking.getField();
        User owner = field.getCluster().getOwner(); // giả sử FootballField -> Cluster -> Owner

        String message = String.format(
                "Khách hàng %s đã thanh toán %.0f VNĐ cho đơn đặt %s khung giờ %s - %s ngày %s",
                customer.getFullName(),
                booking.getTotalPrice(),
                field.getName(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getBookingDate()
        );

        Notification notification = Notification.builder()
                .user(owner) // chủ sân nhận thông báo
                .message(message)
                .status(Notification.Status.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

}
