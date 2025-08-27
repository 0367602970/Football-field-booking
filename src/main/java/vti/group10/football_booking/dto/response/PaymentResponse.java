package vti.group10.football_booking.dto.response;

import lombok.Builder;
import lombok.Data;
import vti.group10.football_booking.model.Payment;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Integer id;
    private Integer bookingId;
    private Double amount;
    private Payment.Method paymentMethod;
    private Payment.Status status;
    private LocalDateTime paymentDate;
}
