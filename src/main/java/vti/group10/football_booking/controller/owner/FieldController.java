package vti.group10.football_booking.controller.owner;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.ApiResponse;
import vti.group10.football_booking.dto.request.FieldRequest;
import vti.group10.football_booking.dto.request.FieldUpdateRequest;
import vti.group10.football_booking.dto.response.FieldResponse;
import vti.group10.football_booking.service.owner.FieldService;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class FieldController {
    private final FieldService fieldService;

    @PostMapping("/fields")
    public ResponseEntity<ApiResponse<FieldResponse>> createField(
            @Valid @RequestBody FieldRequest req,
            HttpServletRequest request) {
        FieldResponse res = fieldService.createField(req);
        return ResponseEntity.ok(ApiResponse.ok(res, "Field created successfully", request.getRequestURI()));
    }

    @PutMapping("fields/{id}")
    public ResponseEntity<ApiResponse<FieldResponse>> updateField(@PathVariable Integer id,
            @RequestBody FieldUpdateRequest req, HttpServletRequest request) {
        FieldResponse updated = fieldService.updateField(id, req);
        return ResponseEntity.ok(
                ApiResponse.ok(updated, "Field updated successfully", request.getRequestURI()));
    }

    @DeleteMapping("/fields/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteField(
            @PathVariable Integer id, HttpServletRequest request) {
        fieldService.deleteField(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Field deleted successfully", request.getRequestURI()));
    }
}
