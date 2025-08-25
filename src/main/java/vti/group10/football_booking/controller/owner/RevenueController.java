package vti.group10.football_booking.controller.owner;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.ApiResponse;
import vti.group10.football_booking.dto.response.RevenueStatsResponse;
import vti.group10.football_booking.service.owner.RevenueService;

@Controller
@RequestMapping("/api/owner/revenue")
@RequiredArgsConstructor
public class RevenueController {
    private final RevenueService revenueService;

    @GetMapping("/{fieldId}")
    public ResponseEntity<ApiResponse<RevenueStatsResponse>> getRevenue(
            @PathVariable Long fieldId,
            @RequestParam(defaultValue = "daily") String type,
            HttpServletRequest request) {

        RevenueStatsResponse stats = revenueService.getRevenue(fieldId, type);

        return ResponseEntity.ok(
                ApiResponse.ok(stats, "Revenue stats retrieved successfully", request.getRequestURI())
        );
    }

    @GetMapping("/{fieldId}/range")
    public ResponseEntity<ApiResponse<RevenueStatsResponse>> getRevenueByRange(
            @PathVariable Long fieldId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            HttpServletRequest request) {

        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(
                            HttpStatus.BAD_REQUEST.value(),
                            "endDate must be after or equal to startDate",
                            request.getRequestURI()
                    )
            );
        }

        RevenueStatsResponse stats = revenueService.getRevenueByRange(fieldId, startDate, endDate);

        return ResponseEntity.ok(
                ApiResponse.ok(stats, "Revenue stats retrieved successfully", request.getRequestURI())
        );
    }
}
