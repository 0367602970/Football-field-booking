package vti.group10.football_booking.dto.request;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldClusterRequest {
    private String name;
    private String address;
    private String district;
    private String city;
    private Double latitude;
    private Double longitude;

    private Integer ownerId; // đổi sang wrapper để tránh lỗi null

    @Builder.Default
    private List<FieldImageRequest> images = new ArrayList<>();

    @Builder.Default
    private List<FieldRequest> fields = new ArrayList<>(); // sân con
}
