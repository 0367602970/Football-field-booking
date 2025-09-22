package vti.group10.football_booking.service;

import java.util.List;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.dto.response.NotificationResponse;
import vti.group10.football_booking.model.Notification;
import vti.group10.football_booking.repository.NotificationRepository;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationResponse> getNotificationsByUserId(Integer userId) {
        return notificationRepository.findByUserIdOrderByIdDesc(userId)
                .stream()
                .map(n -> new NotificationResponse(
                        n.getId(),
                        n.getMessage(),
                        n.getStatus().name(),
                        n.getCreatedAt().toString(),
                        n.getUser().getId(),
                        n.getUser().getFullName()
                ))
                .toList();
    }

    public NotificationResponse updateStatus(Integer notificationId, String status) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        notification.setStatus(Notification.Status.valueOf(status.toUpperCase()));
        notificationRepository.save(notification);

        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.getStatus().name(),
                notification.getCreatedAt().toString(),
                notification.getUser().getId(),
                notification.getUser().getFullName()
        );
    }
}
