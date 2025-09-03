package vti.group10.football_booking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "🏈 Football Booking System API");
        response.put("version", "1.0.0");
        response.put("status", "Running");
        response.put("database", "MySQL");
        response.put("endpoints", Map.of(
            "health", "/actuator/health",
            "users", "/api/users",
            "fields", "/api/fields",
            "bookings", "/api/bookings",
            "payments", "/api/payments",
            "notifications", "/api/notifications"
        ));
        return response;
    }
}