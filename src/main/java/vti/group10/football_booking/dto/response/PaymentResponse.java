package vti.group10.football_booking.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Integer id;
    private Double amount;
    private String paymentMethod;
    private String status;
    private LocalDateTime paymentDate;
    private LocalDateTime paymentConfirmedAt;

    // Booking info
    private Integer bookingId;
    private String bookingStatus;
    private LocalDate bookingDate;
    private String startTime;
    private String endTime;

    // User info
    private Integer userId;
    private String userFullName;
    private String userEmail;

    // Field info
    private Integer fieldId;
    private String fieldName;
    private String fieldAddress;
    private String fieldDistrict;
    private String fieldCity;
}
