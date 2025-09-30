package vti.group10.football_booking.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.config.security.CustomUserDetails;
import vti.group10.football_booking.dto.request.RatingRequest;
import vti.group10.football_booking.dto.response.RatingResponse;
import vti.group10.football_booking.model.FieldCluster;
import vti.group10.football_booking.model.Rating;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.FieldClusterRepository;
import vti.group10.football_booking.repository.UserRepository;
import vti.group10.football_booking.service.user.RatingService;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;
    private final FieldClusterRepository fieldClusterRepository;
    private final UserRepository userRepository;

    // Thêm/cập nhật rating
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createRating(@RequestBody RatingRequest request) {
        try {
            CustomUserDetails userDetails = getCurrentUser();
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }

            RatingResponse rating = ratingService.createRating(userDetails.getId(), request);
            return ResponseEntity.ok(rating);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Create rating failed: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateRating(
            @PathVariable Integer id,
            @RequestBody RatingRequest request) {
        try {
            CustomUserDetails userDetails = getCurrentUser();
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }

            RatingResponse updated = ratingService.updateRating(id, userDetails.getId(), request);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Update rating failed: " + e.getMessage());
        }
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        return null;
    }

    // Lấy danh sách rating theo field cluster
    @GetMapping("/field/{fieldClusterId}")
    public List<RatingResponse> getRatingsByFieldCluster(@PathVariable Integer fieldClusterId) {
        FieldCluster fieldCluster = fieldClusterRepository.findById(fieldClusterId)
                .orElseThrow(() -> new RuntimeException("Field cluster not found"));

        return ratingService.getRatingsByFieldCluster(fieldCluster);
    }

    // Lấy trung bình rating
    @GetMapping("/field/{fieldClusterId}/average")
    public double getAverageRating(@PathVariable Integer fieldClusterId) {
        FieldCluster fieldCluster = fieldClusterRepository.findById(fieldClusterId)
                .orElseThrow(() -> new RuntimeException("Field cluster not found"));

        return ratingService.getAverageRating(fieldCluster);
    }

    // Xóa rating
    @DeleteMapping("/{id}")
    public void deleteRating(@PathVariable Integer id) {
        ratingService.deleteRating(id);
    }
}

