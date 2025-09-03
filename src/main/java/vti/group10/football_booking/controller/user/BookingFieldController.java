package vti.group10.football_booking.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/field/{fieldId}")
    public ResponseEntity<?> getBookingsByFieldAndDate(
            @PathVariable Integer fieldId,
            @RequestParam("bookingDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate
    ) {
        try {
            // Lấy danh sách booking theo sân, ngày, và trạng thái PENDING/CONFIRMED
            List<Booking> bookings = bookingService.getBookingsByFieldAndDateAndStatus(
                    fieldId,
                    bookingDate,
                    Arrays.asList(Booking.Status.PENDING, Booking.Status.CONFIRMED)
            );

            List<BookingResponseDTO> dtos = bookings.stream()
                    .map(this::mapToDTO)
                    .toList();

            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Get bookings failed: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingFieldRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // Kiểm tra role
            boolean hasUserRole = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_USER"));
            if (!hasUserRole) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only accounts with role USER can book a field.");
            }

            // Lấy userId từ principal (nếu principal là custom UserDetails có getId)
            Integer userId = ((CustomUserDetails) auth.getPrincipal()).getId();

            Booking booking = bookingService.createBooking(userId, request);
            BookingResponseDTO dto = mapToDTO(booking);
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Booking failed: " + e.getMessage());
        }
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Integer bookingId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            boolean hasUserRole = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_USER"));
            if (!hasUserRole) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only accounts with role USER can cancel a booking.");
            }

            Integer userId = ((CustomUserDetails) auth.getPrincipal()).getId();

            Booking booking = bookingService.cancelBooking(userId, bookingId);

            BookingResponseDTO dto = mapToDTO(booking);
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Cancel booking failed: " + e.getMessage());
        }
    }

    private BookingResponseDTO mapToDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());

        dto.setUserId(booking.getUser().getId());
        dto.setUserFullName(booking.getUser().getFullName());
        dto.setUserEmail(booking.getUser().getEmail());

        dto.setFieldId(booking.getField().getId());
        dto.setFieldName(booking.getField().getName());

        dto.setBookingDate(booking.getBookingDate());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());

        return dto;
    }
}


//package vti.group10.football_booking.controller.user;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import vti.group10.football_booking.dto.request.BookingFieldRequest;
//import vti.group10.football_booking.model.Booking;
//import vti.group10.football_booking.service.user.BookingFieldService;
//import vti.group10.football_booking.service.JwtService;
//import io.jsonwebtoken.Claims;
//
//@RestController
//@RequestMapping("/api/bookings")
//@RequiredArgsConstructor
//public class BookingFieldController {
//
//    private final BookingFieldService bookingService;
//    private final JwtService jwtService;
//
//    @PostMapping
//    public ResponseEntity<?> createBooking(
//            @RequestHeader("Authorization") String authorizationHeader,
//            @RequestBody BookingFieldRequest request
//    ) {
//        try {
//            // Lấy token từ header Authorization
//            String token = authorizationHeader.replace("Bearer ", "").trim();
//
//            // Parse token để lấy claims
//            Claims claims = jwtService.parseToken(token).getPayload();
//
//            // Lấy roles từ claims (do generate token là List)
//            Object rolesObj = claims.get("roles");
//
//            String role = null;
//            if (rolesObj instanceof java.util.List) {
//                java.util.List<?> rolesList = (java.util.List<?>) rolesObj;
//                if (!rolesList.isEmpty()) {
//                    role = rolesList.get(0).toString(); // Lấy ROLE_USER hoặc ROLE_OWNER...
//                }
//            } else if (rolesObj instanceof String) {
//                role = rolesObj.toString(); // fallback nếu chỉ lưu string
//            }
//            // Chỉ cho phép role USER
//            if (role == null || !role.equalsIgnoreCase("ROLE_USER")) {
//                return ResponseEntity
//                        .status(HttpStatus.FORBIDDEN)
//                        .body("Only accounts with role USER can book a field.");
//            }
//
//            // Lấy userId từ subject
//            Integer userId = Integer.valueOf(claims.getSubject());
//
//            // Gọi service tạo booking
//            Booking booking = bookingService.createBooking(userId, request);
//
//            return ResponseEntity.ok(booking);
//
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Booking failed: " + e.getMessage());
//        }
//    }
//    @PutMapping("/{bookingId}/cancel")
//    public ResponseEntity<?> cancelBooking(
//            @RequestHeader("Authorization") String authorizationHeader,
//            @PathVariable Integer bookingId
//    ) {
//        try {
//            String token = authorizationHeader.replace("Bearer ", "").trim();
//            Claims claims = jwtService.parseToken(token).getPayload();
//
//            Object rolesObj = claims.get("roles");
//            String role = null;
//            if (rolesObj instanceof java.util.List) {
//                java.util.List<?> rolesList = (java.util.List<?>) rolesObj;
//                if (!rolesList.isEmpty()) {
//                    role = rolesList.get(0).toString();
//                }
//            } else if (rolesObj instanceof String) {
//                role = rolesObj.toString();
//            }
//
//            if (role == null || !role.equalsIgnoreCase("ROLE_USER")) {
//                return ResponseEntity
//                        .status(HttpStatus.FORBIDDEN)
//                        .body("Only accounts with role USER can cancel a booking.");
//            }
//
//            Integer userId = Integer.valueOf(claims.getSubject());
//
//            Booking booking = bookingService.cancelBooking(userId, bookingId);
//
//            return ResponseEntity.ok(booking);
//
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Cancel booking failed: " + e.getMessage());
//        }
//    }
//}