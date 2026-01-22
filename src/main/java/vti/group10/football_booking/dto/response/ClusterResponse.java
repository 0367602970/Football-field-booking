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
public class ClusterResponse {
    private Integer id;
    private String name;
    private String address;
    private String district;
    private String city;
    private Double latitude;
    private Double longitude;
    private Integer ownerId;

    // Mô tả giá (ví dụ: "Sân con từ 100k đến 200k")
    private String priceRangeDescription;

    // Danh sách ảnh cụm sân
    private List<String> imageUrls;

    // Danh sách sân con (không lặp lại address/city/district vì đã có ở cluster)
    private List<FieldResponse> fields;
}
