package vti.group10.football_booking.controller.owner;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.ApiResponse;
import vti.group10.football_booking.dto.request.ScheduleRequest;
import vti.group10.football_booking.dto.response.ScheduleResponse;
import vti.group10.football_booking.service.owner.FieldScheduleService;

@Controller
@RequestMapping("/api/owner/fields")
@RequiredArgsConstructor
public class FieldScheduleController {
        private final FieldScheduleService scheduleService;

        @PostMapping("/{fieldId}/schedules")
        public ResponseEntity<ApiResponse<ScheduleResponse>> addSchedule(
                        @PathVariable int fieldId,
                        @RequestBody ScheduleRequest req,
                        HttpServletRequest request) {

                ScheduleResponse res = scheduleService.addSchedule(fieldId, req);
                return ResponseEntity.ok(
                                ApiResponse.ok(res, "Schedule created successfully", request.getRequestURI()));
        }

        @PutMapping("/{fieldId}/schedules/{scheduleId}")
        public ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(
                        @PathVariable int fieldId,
                        @PathVariable int scheduleId,
                        @RequestBody ScheduleRequest req,
                        HttpServletRequest request) {

                ScheduleResponse res = scheduleService.updateSchedule(scheduleId, req);
                return ResponseEntity.ok(
                                ApiResponse.ok(res, "Schedule updated successfully", request.getRequestURI()));
        }

        @DeleteMapping("/{fieldId}/schedules/{scheduleId}")
        public ResponseEntity<ApiResponse<Void>> deleteSchedule(
                        @PathVariable int fieldId,
                        @PathVariable int scheduleId,
                        HttpServletRequest request) {

                scheduleService.deleteSchedule(scheduleId);
                return ResponseEntity.ok(
                                ApiResponse.ok(null, "Schedule deleted successfully", request.getRequestURI()));
        }

        @GetMapping("/{fieldId}/schedules")
        public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getSchedules(
                        @PathVariable int fieldId,
                        HttpServletRequest request) {

                List<ScheduleResponse> schedules = scheduleService.getSchedulesByField(fieldId);
                return ResponseEntity.ok(
                                ApiResponse.ok(schedules, "Schedules retrieved successfully", request.getRequestURI()));
        }

}
