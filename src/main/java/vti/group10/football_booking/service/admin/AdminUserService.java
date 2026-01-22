package vti.group10.football_booking.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.dto.request.UserRequest;
import vti.group10.football_booking.dto.response.UserResponse;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return users.stream()
                .filter(u -> u.getVisible() == User.YesNo.YES)
                .map(u -> new UserResponse(
                        u.getId(),
                        u.getEmail(),
                        u.getFullName(),
                        u.getPhone(),
                        u.getRole().name(),
                        u.getCreatedAt() != null ? u.getCreatedAt().format(formatter) : null
                ))
                .collect(Collectors.toList());
    }
    // --- Sửa user ---
    public UserResponse updateUser(Integer userId, UserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getRole() != null) user.setRole(User.Role.valueOf(request.getRole()));
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword())); // ✅ mã hoá
        }
        User updated = userRepository.save(user);

        return new UserResponse(
                updated.getId(),
                updated.getEmail(),
                updated.getFullName(),
                updated.getPhone(),
                updated.getRole().name(),
                updated.getCreatedAt() != null ? updated.getCreatedAt().toString() : null
        );
    }

    // --- Xóa user ---
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.setVisible(User.YesNo.NO);
        userRepository.save(user);
    }
}
