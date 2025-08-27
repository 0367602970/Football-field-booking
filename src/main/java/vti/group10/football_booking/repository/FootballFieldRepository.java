package vti.group10.football_booking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.model.FootballField;

@Repository
public interface FootballFieldRepository extends JpaRepository<FootballField, Integer> {

    FieldImage save(FieldImage image);

    Optional<FootballField> findById(int fieldId);
    
}
