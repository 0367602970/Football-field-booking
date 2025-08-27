package vti.group10.football_booking.dto.request;

import lombok.Data;
import vti.group10.football_booking.model.Payment;

@Data
public class PaymentRequest {
    private Integer bookingId;
    private Double amount;
    private Payment.Method paymentMethod;
}
