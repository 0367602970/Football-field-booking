package vti.group10.football_booking.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldResponse {
    private int id;
    private String name;
    private String location;
    private String description;
    private Double pricePerHour;
    private String status;
    private List<String> images;
}
