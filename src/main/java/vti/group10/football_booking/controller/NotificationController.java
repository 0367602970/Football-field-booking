package vti.group10.football_booking.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import vti.group10.football_booking.config.security.CustomUserDetails;
import vti.group10.football_booking.dto.response.NotificationResponse;
import vti.group10.football_booking.model.Notification;
import vti.group10.football_booking.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ✅ Lấy danh sách notification theo user hiện tại
    @GetMapping
    public List<NotificationResponse> getMyNotifications() {
        // Ví dụ nếu bạn dùng Spring Security JWT:
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer userId = userDetails.getId();

        return notificationService.getNotificationsByUserId(userId);
    }
    @PutMapping("/{id}/status")
    public NotificationResponse updateNotificationStatus(
            @PathVariable Integer id,
            @RequestParam String status // "READ" hoặc "UNREAD"
    ) {
        return notificationService.updateStatus(id, status);
    }
}
