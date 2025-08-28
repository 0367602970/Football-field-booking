package vti.group10.football_booking.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.request.BookingFieldRequest;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.service.user.BookingFieldService;
import vti.group10.football_booking.service.JwtService;
import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingFieldController {

    private final BookingFieldService bookingService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody BookingFieldRequest request
    ) {
        try {
            // Lấy token từ header Authorization
            String token = authorizationHeader.replace("Bearer ", "").trim();

            // Parse token để lấy claims
            Claims claims = jwtService.parseToken(token).getPayload();

            // Lấy roles từ claims (do generate token là List)
            Object rolesObj = claims.get("roles");

            String role = null;
            if (rolesObj instanceof java.util.List) {
                java.util.List<?> rolesList = (java.util.List<?>) rolesObj;
                if (!rolesList.isEmpty()) {
                    role = rolesList.get(0).toString(); // Lấy ROLE_USER hoặc ROLE_OWNER...
                }
            } else if (rolesObj instanceof String) {
                role = rolesObj.toString(); // fallback nếu chỉ lưu string
            }
            // Chỉ cho phép role USER
            if (role == null || !role.equalsIgnoreCase("ROLE_USER")) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("Only accounts with role USER can book a field.");
            }

            // Lấy userId từ subject
            Integer userId = Integer.valueOf(claims.getSubject());

            // Gọi service tạo booking
            Booking booking = bookingService.createBooking(userId, request);

            return ResponseEntity.ok(booking);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Booking failed: " + e.getMessage());
        }
    }
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