package vti.group10.football_booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FieldImageRequest {
    @NotBlank
    private String imageUrl;
}
