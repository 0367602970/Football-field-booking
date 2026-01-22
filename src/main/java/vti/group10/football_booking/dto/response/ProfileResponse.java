package vti.group10.football_booking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse {
    private String fullName;
    private String email;
    private String phone;
}
