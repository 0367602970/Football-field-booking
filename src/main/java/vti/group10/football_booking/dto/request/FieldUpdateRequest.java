package vti.group10.football_booking.dto.request;

import lombok.Data;

@Data
public class FieldUpdateRequest {
    private String name;
    private String description;
    private Double pricePerHour;
    private String status; // AVAILABLE hoặc MAINTENANCE
    private int ownerId;
}
