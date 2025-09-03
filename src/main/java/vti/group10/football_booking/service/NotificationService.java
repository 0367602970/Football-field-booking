package vti.group10.football_booking.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.model.Notification;
import vti.group10.football_booking.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    public Optional<Notification> getNotificationById(Integer id) {
        return notificationRepository.findById(id);
    }
    
    public List<Notification> getNotificationsByUserId(Integer userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<Notification> getNotificationsByStatus(Notification.Status status) {
        return notificationRepository.findByStatus(status);
    }
    
    public Notification createNotification(Notification notification) {
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }
        return notificationRepository.save(notification);
    }
    
    public Notification markAsRead(Integer id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setStatus(Notification.Status.READ);
        return notificationRepository.save(notification);
    }
    
    public void deleteNotification(Integer id) {
        notificationRepository.deleteById(id);
    }
    @Transactional
    public void deleteNotificationsByUserId(Integer userId) {
        notificationRepository.deleteByUserId(userId);
    }
}