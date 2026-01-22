package vti.group10.football_booking.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vti.group10.football_booking.dto.ApiResponse;
import vti.group10.football_booking.dto.BookingDTO;
import vti.group10.football_booking.service.owner.BookingService;

import java.util.List;

@Controller
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {
    private final BookingService bookingService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getAllBookings(
            HttpServletRequest request) {
        List<BookingDTO> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(
                ApiResponse.ok(bookings, "Get all bookings successful", request.getRequestURI()));
    }
}
