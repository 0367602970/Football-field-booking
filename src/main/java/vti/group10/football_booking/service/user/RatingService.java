package vti.group10.football_booking.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.dto.request.RatingRequest;
import vti.group10.football_booking.dto.response.RatingResponse;
import vti.group10.football_booking.model.FieldCluster;
import vti.group10.football_booking.model.Rating;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.FieldClusterRepository;
import vti.group10.football_booking.repository.RatingRepository;
import vti.group10.football_booking.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private FieldClusterRepository fieldClusterRepository;

    @Autowired
    private UserRepository userRepository;

    // Thêm hoặc cập nhật rating (mỗi user 1 rating/field cluster)
    public RatingResponse createRating(Integer userId, RatingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FieldCluster fieldCluster = fieldClusterRepository.findById(request.getFieldClusterId())
                .orElseThrow(() -> new RuntimeException("FieldCluster not found"));

        Rating rating = new Rating();
        rating.setUser(user);
        rating.setFieldCluster(fieldCluster);
        rating.setRating(request.getScore());
        rating.setComment(request.getComment());

        Rating saved = ratingRepository.save(rating);
        return mapToDTO(saved);
    }

    public RatingResponse updateRating(Integer ratingId, Integer userId, RatingRequest request) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        if (!rating.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền sửa rating này");
        }

        rating.setRating(request.getScore());
        rating.setComment(request.getComment());

        Rating updated = ratingRepository.save(rating);
        return mapToDTO(updated);
    }

    // Lấy tất cả rating theo field cluster
    public List<RatingResponse> getRatingsByFieldCluster(FieldCluster fieldCluster) {
        List<Rating> ratings = ratingRepository.findByFieldCluster(fieldCluster);

        return ratings.stream()
                .map(this::mapToDTO)
                .toList();
    }

    // Lấy rating theo user + field cluster
    public Optional<Rating> getRatingByUserAndFieldCluster(FieldCluster fieldCluster, User user) {
        return ratingRepository.findByFieldClusterAndUser(fieldCluster, user);
    }

    // Xóa rating
    public void deleteRating(Integer id) {
        ratingRepository.deleteById(id);
    }

    // Tính trung bình rating của 1 field cluster
    public double getAverageRating(FieldCluster fieldCluster) {
        List<Rating> ratings = ratingRepository.findByFieldCluster(fieldCluster);
        if (ratings.isEmpty()) return 0.0;
        return ratings.stream().mapToInt(Rating::getRating).average().orElse(0.0);
    }


    private RatingResponse mapToDTO(Rating rating) {
        RatingResponse dto = new RatingResponse();
        dto.setId(rating.getId());
        dto.setScore(rating.getRating());
        dto.setComment(rating.getComment());
        dto.setUserId(rating.getUser().getId());
        dto.setUserName(rating.getUser().getFullName());
        dto.setFieldClusterId(rating.getFieldCluster().getId());
        dto.setUpdatedAt(rating.getUpdatedAt());
        return dto;
    }
}
