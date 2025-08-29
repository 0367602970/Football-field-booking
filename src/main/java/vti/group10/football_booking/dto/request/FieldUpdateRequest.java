package vti.group10.football_booking.dto.request;

import lombok.Data;

@Data
public class FieldUpdateRequest {
    private String name;
    private String address;
    private String district;
    private String city;
    private String description;
    private Double pricePerHour;
    private String status;
}
