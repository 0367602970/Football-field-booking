package vti.group10.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vti.group10.football_booking.model.FieldCluster;
import vti.group10.football_booking.model.Rating;
import vti.group10.football_booking.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    // Lấy tất cả rating theo field cluster
    List<Rating> findByFieldCluster(FieldCluster fieldCluster);

    // Lấy rating của 1 user cho 1 field cluster (unique)
    Optional<Rating> findByFieldClusterAndUser(FieldCluster fieldCluster, User user);

    // Tính trung bình rating theo field cluster
    // Có thể viết custom query nếu muốn AVG()
}

