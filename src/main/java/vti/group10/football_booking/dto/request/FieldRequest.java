package vti.group10.football_booking.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldRequest {
    private String name;
    private String description;
    private Double pricePerHour;
    private String status; // AVAILABLE hoặc MAINTENANCE
    private Integer clusterId; // id của cụm sân
}
