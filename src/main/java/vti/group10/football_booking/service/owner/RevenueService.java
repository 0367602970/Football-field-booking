package vti.group10.football_booking.service.owner;

import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.response.RevenueStatsResponse;
import vti.group10.football_booking.repository.BookingRepository;

@Service
@RequiredArgsConstructor
public class RevenueService {
    private final BookingRepository bookingRepo;

    public RevenueStatsResponse getRevenue(int fieldId, String type) {
        LocalDate today = LocalDate.now();
        LocalDate start, end;

        switch (type.toLowerCase()) {
            case "daily":
                start = today;
                end = today;
                break;
            case "weekly":
                start = today.with(DayOfWeek.MONDAY);
                end = today.with(DayOfWeek.SUNDAY);
                break;
            case "monthly":
                start = today.withDayOfMonth(1);
                end = today.withDayOfMonth(today.lengthOfMonth());
                break;
            default:
                throw new IllegalArgumentException("Invalid report type: " + type);
        }

        return buildStats(fieldId, start, end, type);
    }

    public RevenueStatsResponse getRevenueByRange(int fieldId, LocalDate start, LocalDate end) {
        return buildStats(fieldId, start, end, "custom");
    }

    private RevenueStatsResponse buildStats(int fieldId, LocalDate start, LocalDate end, String type) {
        Double totalRevenue = bookingRepo.calculateRevenue(fieldId, start, end);
        Long totalBookings = bookingRepo.countBookings(fieldId, start, end);

        return RevenueStatsResponse.builder()
                .period(type)
                .startDate(start)
                .endDate(end)
                .totalRevenue(totalRevenue != null ? totalRevenue : 0.0)
                .totalBookings(totalBookings != null ? totalBookings : 0L)
                .build();
    }
}
