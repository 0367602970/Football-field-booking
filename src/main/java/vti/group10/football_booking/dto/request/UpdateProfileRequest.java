package vti.group10.football_booking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "Full name cannot be blank")
    private String fullName;
    private String phone;
    private String oldPassword;
    private String newPassword;
}
