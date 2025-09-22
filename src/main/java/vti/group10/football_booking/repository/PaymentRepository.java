package vti.group10.football_booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vti.group10.football_booking.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByMomoOrderId(String momoOrderId);
    @Query("""
    SELECT p FROM Payment p
    JOIN p.booking b
    JOIN b.field f
    JOIN f.cluster c
    JOIN c.owner o
    WHERE o.id = :ownerId
      AND f.visible = vti.group10.football_booking.model.FootballField.YesNo.YES
      AND c.visible = vti.group10.football_booking.model.FieldCluster.YesNo.YES
    """)
    List<Payment> findPaymentsByOwnerId(@Param("ownerId") Integer ownerId);
    Optional<Payment> findByBookingId(Integer bookingId);

}
