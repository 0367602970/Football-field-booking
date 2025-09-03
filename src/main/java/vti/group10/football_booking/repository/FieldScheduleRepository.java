package vti.group10.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vti.group10.football_booking.model.FieldSchedule;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FieldScheduleRepository extends JpaRepository<FieldSchedule, Integer> {
    List<FieldSchedule> findByFieldId(Integer fieldId);
    List<FieldSchedule> findByFieldIdAndAvailableDate(Integer fieldId, LocalDate availableDate);
    List<FieldSchedule> findByIsBooked(Boolean isBooked);
    void deleteByFieldId(Integer fieldId);
}