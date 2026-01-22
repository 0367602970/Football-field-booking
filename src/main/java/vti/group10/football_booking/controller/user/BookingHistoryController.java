package vti.group10.football_booking.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.response.BookingHistoryResponse;
import vti.group10.football_booking.service.user.BookingHistoryService;

@RestController
@RequestMapping("/api/user/bookings")
@RequiredArgsConstructor
public class BookingHistoryController {

    private final BookingHistoryService bookingHistoryService;

    @GetMapping("/history")
    public ResponseEntity<Page<BookingHistoryResponse>> getBookingHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookingHistoryService.getBookingHistory(pageable));
    }
}
