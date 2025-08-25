package vti.group10.football_booking.dto.request;

import lombok.Data;

@Data
public class FieldUpdateRequest {
    private String name;
    private String location;
    private String description;
    private Double pricePerHour;
    private String status;
}
