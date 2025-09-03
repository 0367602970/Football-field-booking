package vti.group10.football_booking.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.model.FieldSchedule;
import vti.group10.football_booking.repository.FieldScheduleRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FieldScheduleService {
    
    @Autowired
    private FieldScheduleRepository fieldScheduleRepository;
    
    public List<FieldSchedule> getAllSchedules() {
        return fieldScheduleRepository.findAll();
    }
    
    public Optional<FieldSchedule> getScheduleById(Integer id) {
        return fieldScheduleRepository.findById(id);
    }
    
    public List<FieldSchedule> getSchedulesByFieldId(Integer fieldId) {
        return fieldScheduleRepository.findByFieldId(fieldId);
    }
    
    public List<FieldSchedule> getSchedulesByFieldAndDate(Integer fieldId, LocalDate date) {
        return fieldScheduleRepository.findByFieldIdAndAvailableDate(fieldId, date);
    }
    
    public List<FieldSchedule> getAvailableSchedules() {
        return fieldScheduleRepository.findByIsBooked(false);
    }
    
    public FieldSchedule createSchedule(FieldSchedule schedule) {
        return fieldScheduleRepository.save(schedule);
    }
    
    public FieldSchedule updateSchedule(Integer id, FieldSchedule scheduleDetails) {
        FieldSchedule schedule = fieldScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        
        schedule.setAvailableDate(scheduleDetails.getAvailableDate());
        schedule.setStartTime(scheduleDetails.getStartTime());
        schedule.setEndTime(scheduleDetails.getEndTime());
        schedule.setIsBooked(scheduleDetails.getIsBooked());
        
        return fieldScheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteFieldSchedulesByFieldId(Integer fieldId) {
        fieldScheduleRepository.deleteByFieldId(fieldId);
    }
}