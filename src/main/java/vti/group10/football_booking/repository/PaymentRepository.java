package vti.group10.football_booking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import vti.group10.football_booking.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByMomoOrderId(String momoOrderId);
}
