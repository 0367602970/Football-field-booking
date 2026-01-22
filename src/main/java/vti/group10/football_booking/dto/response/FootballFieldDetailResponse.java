package vti.group10.football_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FootballFieldDetailResponse {
    private Integer id;
    private String name;
    private String address;
    private String district;
    private String city;
    private Double pricePerHour;
    private String description;
    private String status;
    private String ownerName;       // tên chủ sân
    private List<String> imageUrls; // tối đa 8 ảnh
    private Double latitude;
    private Double longitude;
}
