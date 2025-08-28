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
    private String location;
    private Double pricePerHour;
    private String description;
    private String status;
    private List<String> imageUrls; // chỉ trả về link ảnh
}
