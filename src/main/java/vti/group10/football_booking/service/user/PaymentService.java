package vti.group10.football_booking.service.user;

import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.model.Payment;
import vti.group10.football_booking.repository.BookingRepository;
import vti.group10.football_booking.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment saveMomoPayment(Map<String, Object> momoData) {
        // Lấy orderId từ MoMo
        String orderIdFull = (String) momoData.get("orderId");
        // Trong createPayment mình set orderId = bookingId-timestamp → tách bookingId ra
        Integer bookingId = Integer.valueOf(orderIdFull.split("-")[0]);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(Double.valueOf(momoData.get("amount").toString()))
                .paymentMethod(Payment.Method.MOMO)
                .status(((Integer) momoData.get("resultCode")) == 0 
                        ? Payment.Status.SUCCESS 
                        : Payment.Status.FAILED)
                .momoOrderId(orderIdFull)
                .momoRequestId((String) momoData.get("requestId"))
                .momoTransId(momoData.get("transId").toString())
                .resultCode(Integer.valueOf(momoData.get("resultCode").toString()))
                .message((String) momoData.get("message"))
                .build();

        return paymentRepository.save(payment);
    }
}
