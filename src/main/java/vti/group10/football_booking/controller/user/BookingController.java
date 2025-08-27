package vti.group10.football_booking.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.request.BookingRequest;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.service.user.BookingService;
import vti.group10.football_booking.service.JwtService;
import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody BookingRequest request
    ) {
        try {
            String token = authorizationHeader.replace("Bearer ", "").trim();
            Claims claims = jwtService.parseToken(token).getPayload();

            Object rolesObj = claims.get("roles");
            String role = null;
            if (rolesObj instanceof java.util.List) {
                java.util.List<?> rolesList = (java.util.List<?>) rolesObj;
                if (!rolesList.isEmpty()) {
                    role = rolesList.get(0).toString();
                }
            } else if (rolesObj instanceof String) {
                role = rolesObj.toString();
            }

            if (role == null || !role.equalsIgnoreCase("ROLE_USER")) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("Only accounts with role USER can book a field.");
            }

            Integer userId = Integer.valueOf(claims.getSubject());

            Booking booking = bookingService.createBooking(userId, request);

            return ResponseEntity.ok(booking);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Booking failed: " + e.getMessage());
        }
    }

    // ✅ API huỷ booking
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer bookingId
    ) {
        try {
            String token = authorizationHeader.replace("Bearer ", "").trim();
            Claims claims = jwtService.parseToken(token).getPayload();

            Object rolesObj = claims.get("roles");
            String role = null;
            if (rolesObj instanceof java.util.List) {
                java.util.List<?> rolesList = (java.util.List<?>) rolesObj;
                if (!rolesList.isEmpty()) {
                    role = rolesList.get(0).toString();
                }
            } else if (rolesObj instanceof String) {
                role = rolesObj.toString();
            }

            if (role == null || !role.equalsIgnoreCase("ROLE_USER")) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("Only accounts with role USER can cancel a booking.");
            }

            Integer userId = Integer.valueOf(claims.getSubject());

            Booking booking = bookingService.cancelBooking(userId, bookingId);

            return ResponseEntity.ok(booking);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Cancel booking failed: " + e.getMessage());
        }
    }
}
