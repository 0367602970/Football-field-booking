package vti.group10.football_booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.config.security.CustomUserDetails;
import vti.group10.football_booking.dto.request.BookingFieldRequest;
import vti.group10.football_booking.dto.response.BookingResponseDTO;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.service.user.BookingFieldService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingFieldController {

    private final BookingFieldService bookingService;

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Integer bookingId) {
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            return ResponseEntity.ok(mapToDTO(booking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Booking not found with id: " + bookingId);
        }
    }
    @GetMapping("/field/{fieldId}")
    public ResponseEntity<?> getBookingsByFieldAndDate(
            @PathVariable Integer fieldId,
            @RequestParam("bookingDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate
    ) {
        try {
            List<BookingResponseDTO> dtos = bookingService.getBookingsByFieldAndDateAndStatus(
                    fieldId,
                    bookingDate,
                    Arrays.asList(Booking.Status.PENDING, Booking.Status.CONFIRMED)
            );
            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Get bookings failed: " + e.getMessage());
        }
    }

    // =========================
    // Chỉ USER mới được phép đặt/cancel
    // =========================

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createBooking(@RequestBody BookingFieldRequest request) {
        try {
            CustomUserDetails userDetails = getCurrentUser();
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }

            Booking booking = bookingService.createBooking(userDetails.getId(), request);
            return ResponseEntity.ok(mapToDTO(booking));

        } catch (Exception e) {
            e.printStackTrace(); // log chi tiết ra console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Booking failed: " + e.getMessage());
        }
    }

    @PutMapping("/{bookingId}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelBooking(@PathVariable Integer bookingId) {
        try {
            CustomUserDetails userDetails = getCurrentUser();
            Booking booking = bookingService.cancelBooking(userDetails.getId(), bookingId);
            return ResponseEntity.ok(mapToDTO(booking));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Cancel booking failed: " + e.getMessage());
        }
    }

    // ===== Helper Methods =====

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails;
        }
        return null;
    }

    private BookingResponseDTO mapToDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setUserFullName(booking.getUser().getFullName());
        dto.setUserEmail(booking.getUser().getEmail());
        dto.setFieldId(booking.getField().getId());
        dto.setFieldName(booking.getField().getName());

        // Fix NullPointerException
        dto.setClusterName(
                booking.getField().getCluster() != null ? booking.getField().getCluster().getName() : null
        );

        dto.setBookingDate(booking.getBookingDate());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        dto.setPaymentToken(booking.getPaymentToken());
        return dto;
    }

}
