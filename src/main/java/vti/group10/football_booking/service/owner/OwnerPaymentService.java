package vti.group10.football_booking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.PaymentResponse;
import vti.group10.football_booking.model.Payment;
import vti.group10.football_booking.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class OwnerPaymentService {

    private final PaymentRepository paymentRepository;

    public List<PaymentResponse> getPaymentsByOwner(Integer ownerId) {
        List<Payment> payments = paymentRepository.findPaymentsByOwnerId(ownerId);

        return payments.stream().map(p -> {
            var b = p.getBooking();
            var f = b.getField();
            var u = b.getUser();
            var c = f.getCluster();

            return PaymentResponse.builder()
                    .id(p.getId())
                    .amount(p.getAmount())
                    .paymentMethod(p.getPaymentMethod().name())
                    .status(p.getStatus().name())
                    .paymentDate(p.getPaymentDate())
                    .bookingId(b.getId())
                    .bookingStatus(b.getStatus().name())
                    .bookingDate(b.getBookingDate())
                    .startTime(b.getStartTime().toString())
                    .endTime(b.getEndTime().toString())
                    .userId(u != null ? u.getId() : null)
                    .userFullName(u != null ? u.getFullName() : null)
                    .userEmail(u != null ? u.getEmail() : null)
                    .fieldId(f != null ? f.getId() : null)
                    .fieldName(f != null ? f.getName() : null)
                    .fieldAddress(c != null ? c.getAddress() : null)
                    .fieldDistrict(c != null ? c.getDistrict() : null)
                    .fieldCity(c != null ? c.getCity() : null)
                    .build();
        }).collect(Collectors.toList());
    }
}
