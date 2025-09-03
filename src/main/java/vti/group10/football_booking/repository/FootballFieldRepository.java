package vti.group10.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vti.group10.football_booking.model.FootballField;

import java.util.List;

@Repository
public interface FootballFieldRepository extends JpaRepository<FootballField, Integer> {
    List<FootballField> findByStatus(FootballField.Status status);
    List<FootballField> findByAddressContainingIgnoreCase(String address);
    List<FootballField> findByNameContainingIgnoreCase(String name);
    List<FootballField> findByCity(String city);
    List<FootballField> findByDistrict(String district);
    List<FootballField> findByOwnerId(Integer ownerId);
}