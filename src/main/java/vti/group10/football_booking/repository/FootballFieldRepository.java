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

    //Tìm kiếm sân theo keyword (name hoặc location)
    @Query("SELECT f FROM FootballField f " +
            "WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(f.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<FootballField> searchFootballField(@Param("keyword") String keyword);

    Page<FootballField> findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String name, String location, Pageable pageable);

}
