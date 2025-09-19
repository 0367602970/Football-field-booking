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
public class FootballFieldResponse {
    private Integer id;
    private String name;
    private Double pricePerHour;
    private String status;
    private ClusterResponse cluster;      // thông tin cụm sân
    private List<String> imageUrls;       // chỉ trả về link ảnh

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClusterResponse {
        private Integer id;
        private String name;
        private String address;
        private String district;
        private String city;
    }
}
