package vti.group10.football_booking.service.owner;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.request.ScheduleRequest;
import vti.group10.football_booking.dto.response.ScheduleResponse;
import vti.group10.football_booking.model.FieldSchedule;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.repository.FieldScheduleRepository;
import vti.group10.football_booking.repository.FootballFieldRepository;

@Service
@RequiredArgsConstructor
public class FieldScheduleService {
    private final FootballFieldRepository fieldRepo;
    private final FieldScheduleRepository scheduleRepo;

    // Thêm lịch mới
    public ScheduleResponse addSchedule(Integer fieldId, ScheduleRequest req) {
        FootballField field = fieldRepo.findById(fieldId)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        FieldSchedule schedule = FieldSchedule.builder()
                .field(field)
                .availableDate(req.getAvailableDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .isBooked(false)
                .build();

        return toDto(scheduleRepo.save(schedule));
    }

    // Sửa lịch
    public ScheduleResponse updateSchedule(Integer id, ScheduleRequest req) {
        FieldSchedule schedule = scheduleRepo.findById( id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        if (req.getAvailableDate() != null)
            schedule.setAvailableDate(req.getAvailableDate());
        if (req.getStartTime() != null)
            schedule.setStartTime(req.getStartTime());
        if (req.getEndTime() != null)
            schedule.setEndTime(req.getEndTime());

        return toDto(scheduleRepo.save(schedule));
    }

    // Xóa lịch
    public void deleteSchedule(Integer id) {
        FieldSchedule schedule = scheduleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        scheduleRepo.delete(schedule);
    }

    // Lấy danh sách lịch của 1 sân
    public List<ScheduleResponse> getSchedulesByField(Integer fieldId) {
        return scheduleRepo.findByFieldId(fieldId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<ScheduleResponse> getBookedSchedulesByDate(Integer fieldId, LocalDate date) {
        return scheduleRepo.findByFieldIdAndAvailableDateAndIsBookedTrue(fieldId, date)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Mapper
    private ScheduleResponse toDto(FieldSchedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .availableDate(schedule.getAvailableDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .isBooked(schedule.getIsBooked())
                .build();
    }
}
