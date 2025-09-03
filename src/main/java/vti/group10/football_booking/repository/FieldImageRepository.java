package vti.group10.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vti.group10.football_booking.model.FieldImage;

import java.util.List;

@Repository
public interface FieldImageRepository extends JpaRepository<FieldImage, Integer> {
    List<FieldImage> findByFieldId(Integer fieldId);
    void deleteByFieldId(Integer fieldId);
}