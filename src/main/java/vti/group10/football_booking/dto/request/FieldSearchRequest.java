package vti.group10.football_booking.dto.request;

import lombok.Data;

@Data
public class FieldSearchRequest {
    private String name;       // tìm theo tên sân
    private String location;   // tìm theo địa điểm
    private Double pricePerHour;   // giá mỗi giờ
    private String status;     // AVAILABLE / MAINTENANCE
}
