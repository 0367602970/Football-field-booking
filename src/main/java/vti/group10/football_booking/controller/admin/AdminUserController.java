package vti.group10.football_booking.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.request.UserRequest;
import vti.group10.football_booking.dto.response.UserResponse;
import vti.group10.football_booking.service.admin.AdminUserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = adminUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    // Cập nhật user
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Integer userId,
            @RequestBody UserRequest request) {
        return ResponseEntity.ok(adminUserService.updateUser(userId, request));
    }

    // Xóa user
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
