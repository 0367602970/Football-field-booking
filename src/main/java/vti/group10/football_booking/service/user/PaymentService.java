package vti.group10.football_booking.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vti.group10.football_booking.dto.request.PaymentRequest;
import vti.group10.football_booking.dto.response.PaymentResponse;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.model.Payment;
import vti.group10.football_booking.repository.BookingRepository;
import vti.group10.football_booking.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(Payment.Status.SUCCESS) // giả sử mặc định thanh toán thành công
                .build();

        paymentRepository.save(payment);

        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(booking.getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}