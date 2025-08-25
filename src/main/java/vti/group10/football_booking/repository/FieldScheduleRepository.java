package vti.group10.football_booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vti.group10.football_booking.model.FieldSchedule;

@Repository
public interface FieldScheduleRepository extends JpaRepository<FieldSchedule, Long> {
    List<FieldSchedule> findByFieldId(Long fieldId);
}
