package vti.group10.football_booking.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vti.group10.football_booking.model.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByBookingId(Integer bookingId);
    List<Payment> findByStatus(Payment.Status status);
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
    @Transactional
    void deleteByBookingId(Integer bookingId);
}