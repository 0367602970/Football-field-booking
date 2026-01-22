package vti.group10.football_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.model.Payment;
import vti.group10.football_booking.repository.BookingRepository;
import vti.group10.football_booking.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingStatusScheduler {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    // ✅ chạy mỗi 1 phút
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelExpiredBookings() {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);

        // Tìm các booking pending quá 1 phút
        List<Booking> expiredBookings = bookingRepository.findByStatusAndCreatedAtBefore(
                Booking.Status.PENDING, oneMinuteAgo
        );

        if (!expiredBookings.isEmpty()) {
            expiredBookings.forEach(booking -> {
                booking.setStatus(Booking.Status.CANCELLED);

                // nếu có payment thì set FAILED
                paymentRepository.findByBookingId(booking.getId())
                        .ifPresent(payment -> {
                            if (payment.getStatus() == Payment.Status.PENDING) {
                                payment.setStatus(Payment.Status.FAILED);
                                payment.setPaymentDate(LocalDateTime.now());
                                paymentRepository.save(payment);
                            }
                        });
            });
            bookingRepository.saveAll(expiredBookings);
            System.out.println("⏱ Đã hủy " + expiredBookings.size() + " booking quá hạn");
        }
    }
}
