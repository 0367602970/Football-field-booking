package vti.group10.football_booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.model.Notification;
import vti.group10.football_booking.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class NotificationController {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Notification getNotificationById(@PathVariable Integer id) {
        return notificationRepository.findById(id).orElse(null);
    }
    
    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }
    
    @PutMapping("/{id}/read")
    public Notification markAsRead(@PathVariable Integer id) {
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setStatus(Notification.Status.READ);
        return notificationRepository.save(notification);
    }
    
    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Integer id) {
        notificationRepository.deleteById(id);
    }
}