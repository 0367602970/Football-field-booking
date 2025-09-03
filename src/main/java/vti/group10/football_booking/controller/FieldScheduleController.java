package vti.group10.football_booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.model.FieldSchedule;
import vti.group10.football_booking.repository.FieldScheduleRepository;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/field-schedules")
@CrossOrigin(originPatterns = {"http://localhost:*", "https://localhost:*"})
public class FieldScheduleController {
    
    @Autowired
    private FieldScheduleRepository fieldScheduleRepository;
    
    @GetMapping
    public List<FieldSchedule> getAllSchedules() {
        return fieldScheduleRepository.findAll();
    }
    
    @GetMapping("/field/{fieldId}")
    public List<FieldSchedule> getSchedulesByFieldId(@PathVariable Integer fieldId) {
        return fieldScheduleRepository.findByFieldId(fieldId);
    }
    
    @GetMapping("/field/{fieldId}/date/{date}")
    public List<FieldSchedule> getSchedulesByFieldAndDate(
            @PathVariable Integer fieldId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return fieldScheduleRepository.findByFieldIdAndAvailableDate(fieldId, date);
    }
    
    @GetMapping("/available")
    public List<FieldSchedule> getAvailableSchedules() {
        return fieldScheduleRepository.findByIsBooked(false);
    }
    
    @PostMapping
    public FieldSchedule createSchedule(@RequestBody FieldSchedule schedule) {
        return fieldScheduleRepository.save(schedule);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FieldSchedule> updateSchedule(@PathVariable Integer id, @RequestBody FieldSchedule scheduleDetails) {
        return fieldScheduleRepository.findById(id)
                .map(schedule -> {
                    schedule.setAvailableDate(scheduleDetails.getAvailableDate());
                    schedule.setStartTime(scheduleDetails.getStartTime());
                    schedule.setEndTime(scheduleDetails.getEndTime());
                    schedule.setIsBooked(scheduleDetails.getIsBooked());
                    return ResponseEntity.ok(fieldScheduleRepository.save(schedule));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Integer id) {
        fieldScheduleRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}