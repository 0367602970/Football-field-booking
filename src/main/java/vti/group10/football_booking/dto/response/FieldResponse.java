package vti.group10.football_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldResponse {
    private Integer id;
    private String name;
    private String description;
    private Double pricePerHour;
    private String status;

    // Thông tin cụm sân
    private Integer clusterId;
    private String clusterName;
    private String clusterAddress;
    private String clusterDistrict;
    private String clusterCity;
}
