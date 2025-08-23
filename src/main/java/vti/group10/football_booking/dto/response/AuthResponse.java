package vti.group10.football_booking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String tokenType;   // "Bearer"
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private Object user;
}
