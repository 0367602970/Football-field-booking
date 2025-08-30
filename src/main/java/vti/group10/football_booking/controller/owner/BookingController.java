package vti.group10.football_booking.controller.owner;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.ApiResponse;
import vti.group10.football_booking.dto.BookingDTO;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.service.owner.BookingService;

@Controller
@RequestMapping("/api/owner/bookings")
@RequiredArgsConstructor
public class BookingController {
        private final BookingService bookingService;

        @GetMapping("/all/{ownerId}")
        public ResponseEntity<ApiResponse<List<BookingDTO>>> getAllBookingsByOwner(
                @PathVariable int ownerId,
                HttpServletRequest request) {
                List<BookingDTO> bookings = bookingService.getAllBookingsByOwner(ownerId);
                return ResponseEntity.ok(
                        ApiResponse.ok(bookings, "Get all bookings successful", request.getRequestURI()));
        }

        @GetMapping("/pending/{ownerId}")
        public ResponseEntity<ApiResponse<List<BookingDTO>>> getPendingBookings(
                @PathVariable int ownerId,
                HttpServletRequest request) {
        List<BookingDTO> bookings = bookingService.getBookingsByOwnerAndStatus(ownerId, Booking.Status.PENDING);
        return ResponseEntity.ok(
                ApiResponse.ok(bookings, "Get bookings successful", request.getRequestURI()));
        }

        @PutMapping("/{bookingId}/status")
        public ResponseEntity<ApiResponse<Booking>> updateBookingStatus(
                        @PathVariable int bookingId,
                        @RequestParam Booking.Status status,
                        HttpServletRequest request) {
                Booking updated = bookingService.updateBookingStatus(bookingId, status);
                return ResponseEntity.ok(
                                ApiResponse.ok(updated, "Booking status updated", request.getRequestURI()));
        }

        @DeleteMapping("/{bookingId}")
        public ResponseEntity<ApiResponse<Void>> deleteBooking(
                @PathVariable int bookingId,
                HttpServletRequest request) {
                bookingService.deleteBooking(bookingId);
                return ResponseEntity.ok(
                        ApiResponse.ok(null, "Booking deleted successfully", request.getRequestURI()));
        }
}
