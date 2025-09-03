package vti.group10.football_booking.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ReportController {
    
    @GetMapping
    public Map<String, Object> getReports() {
        Map<String, Object> reports = new HashMap<>();
        reports.put("totalRevenue", 15750000);
        reports.put("totalBookings", 45);
        reports.put("totalUsers", 128);
        reports.put("totalFields", 8);
        
        List<Map<String, Object>> monthlyRevenue = new ArrayList<>();
        monthlyRevenue.add(Map.of("month", "Jan", "revenue", 5250000));
        monthlyRevenue.add(Map.of("month", "Feb", "revenue", 4800000));
        monthlyRevenue.add(Map.of("month", "Mar", "revenue", 5700000));
        monthlyRevenue.add(Map.of("month", "Apr", "revenue", 6200000));
        monthlyRevenue.add(Map.of("month", "May", "revenue", 5900000));
        monthlyRevenue.add(Map.of("month", "Jun", "revenue", 6800000));
        
        reports.put("monthlyRevenue", monthlyRevenue);
        return reports;
    }
}