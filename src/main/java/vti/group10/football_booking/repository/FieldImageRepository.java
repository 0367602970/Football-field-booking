package vti.group10.football_booking.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vti.group10.football_booking.model.FieldImage;

@Repository
public interface FieldImageRepository extends JpaRepository<FieldImage, Integer> {
    List<FieldImage> findByClusterId(Integer clusterId);
}
