package vti.group10.football_booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String password; // ✅ để update/mã hoá
}
