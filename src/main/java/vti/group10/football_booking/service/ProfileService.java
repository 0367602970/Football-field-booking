package vti.group10.football_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vti.group10.football_booking.dto.request.UpdateProfileRequest;
import vti.group10.football_booking.dto.response.ProfileResponse;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Cập nhật thông tin cá nhân
     */
    @Transactional
    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        // Lấy email user hiện tại từ SecurityContext
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Cập nhật thông tin cơ bản
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        // Xử lý đổi mật khẩu
        if (request.getOldPassword() != null && request.getNewPassword() != null) {
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new RuntimeException("Mật khẩu cũ không đúng");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        // Lưu thay đổi
        userRepository.save(user);

        // Trả về DTO response (không chứa password)
        return toProfileResponse(user);
    }

    /**
     * Hiển thị thông tin cá nhân
     */
    @Transactional(readOnly = true)
    public ProfileResponse getProfile() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return toProfileResponse(user);
    }

    /**
     * Mapper User -> ProfileResponse
     */
    private ProfileResponse toProfileResponse(User user) {
        return ProfileResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }
}
