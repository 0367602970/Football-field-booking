package vti.group10.football_booking.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingHistoryResponse {
    private Integer bookingId;
    private String fieldName;
    private String clusterName;
    private String address;
    private String district;
    private String city;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double totalPrice;
    private String status;
}
