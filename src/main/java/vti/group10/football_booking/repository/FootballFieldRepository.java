package vti.group10.football_booking.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.model.FootballField;

@Repository
public interface FootballFieldRepository extends JpaRepository<FootballField, Integer> {

    FieldImage save(FieldImage image);

    Optional<FootballField> findById(Integer fieldId);

    // Tìm kiếm sân theo keyword (name, address, district hoặc city)
    @Query("SELECT f FROM FootballField f " +
            "WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(f.address) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(f.district) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(f.city) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<FootballField> searchFootballField(@Param("keyword") String keyword);

    // Tìm kiếm phân trang theo name hoặc address hoặc district hoặc city
    Page<FootballField> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCaseOrDistrictContainingIgnoreCaseOrCityContainingIgnoreCase(
            String name, String address, String district, String city, Pageable pageable);

    @Query("SELECT f FROM FootballField f " +
            "WHERE (:city IS NULL OR LOWER(f.city) = LOWER(:city)) " +
            "AND (:district IS NULL OR LOWER(f.district) = LOWER(:district)) " +
            "AND (:pricePerHour IS NULL OR f.pricePerHour <= :pricePerHour)")
    Page<FootballField> filterFootballFields(
            @Param("city") String city,
            @Param("district") String district,
            @Param("pricePerHour") Double pricePerHour,
            Pageable pageable);

}
