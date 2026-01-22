package vti.group10.football_booking.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RevenueStatsResponse {
    private String period;      // "daily", "weekly", "monthly"
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalRevenue;
    private Long totalBookings;
}
