package vti.group10.football_booking.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldImageRequest {
    private String imageUrl;
}
