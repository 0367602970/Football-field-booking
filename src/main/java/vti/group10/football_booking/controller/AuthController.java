package vti.group10.football_booking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.service.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = {"http://localhost:*", "https://localhost:*"})
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            Optional<User> userOpt = userService.getUserByEmail(request.getEmail());
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email không tồn tại"));
            }
            
            User user = userOpt.get();
            
            // For demo purposes, we'll accept any password for existing users
            // In production, you should verify the password properly
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mật khẩu không đúng"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("token", "demo-token-" + user.getId());
            response.put("message", "Đăng nhập thành công");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login error: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Có lỗi xảy ra khi đăng nhập"));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            if (userService.getUserByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email đã tồn tại"));
            }
            
            User user = new User();
            user.setEmail(request.getEmail());
            user.setFullName(request.getFullName());
            user.setPhone(request.getPhone());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(User.Role.USER);
            user.setCreatedAt(LocalDateTime.now());
            
            User savedUser = userService.handleCreateUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", savedUser);
            response.put("message", "Đăng ký thành công");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Register error: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Có lỗi xảy ra khi đăng ký"));
        }
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            Optional<User> userOpt = userService.getUserById(request.getUserId());
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Người dùng không tồn tại"));
            }
            
            User user = userOpt.get();
            
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mật khẩu hiện tại không đúng"));
            }
            
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userService.updateUser(user.getId(), user);
            
            return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
        } catch (Exception e) {
            logger.error("Change password error: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Có lỗi xảy ra khi đổi mật khẩu"));
        }
    }
    
    @PutMapping("/profile/{id}")
    public ResponseEntity<Map<String, Object>> updateProfile(@PathVariable Integer id, @RequestBody UpdateProfileRequest request) {
        try {
            Optional<User> userOpt = userService.getUserById(id);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Người dùng không tồn tại"));
            }
            
            User user = userOpt.get();
            user.setFullName(request.getFullName());
            user.setPhone(request.getPhone());
            
            User updatedUser = userService.updateUser(id, user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", updatedUser);
            response.put("message", "Cập nhật thông tin thành công");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Update profile error: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Có lỗi xảy ra khi cập nhật thông tin"));
        }
    }
    
    // Request DTOs
    public static class LoginRequest {
        private String email;
        private String password;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class RegisterRequest {
        private String email;
        private String password;
        private String fullName;
        private String phone;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
    
    public static class ChangePasswordRequest {
        private Integer userId;
        private String currentPassword;
        private String newPassword;
        
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
    
    public static class UpdateProfileRequest {
        private String fullName;
        private String phone;
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}