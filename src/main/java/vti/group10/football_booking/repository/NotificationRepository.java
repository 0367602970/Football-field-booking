package vti.group10.football_booking.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vti.group10.football_booking.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByIdDesc(Integer userId);
}