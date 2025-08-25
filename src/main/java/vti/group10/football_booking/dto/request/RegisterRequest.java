package vti.group10.football_booking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min=6, max=100, message = "Mật khẩu phải dài hơn 6 ký tự")
    private String password;

    private String fullName;
    private String phone;
    private String role; // "USER" | "OWNER"
}
