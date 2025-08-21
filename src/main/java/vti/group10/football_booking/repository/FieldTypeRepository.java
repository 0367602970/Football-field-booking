package vti.group10.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vti.group10.football_booking.model.FieldType;

@Repository
public interface FieldTypeRepository extends JpaRepository<FieldType, Long> {
    
}
