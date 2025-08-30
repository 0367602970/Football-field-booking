package vti.group10.football_booking.controller.owner;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/fields")
    public ResponseEntity<ApiResponse<Page<FieldResponse>>> getAllFields(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Page<FieldResponse> res = fieldService.getAllFields(page, size);
        return ResponseEntity.ok(
                ApiResponse.ok(res, "Fields fetched successfully", request.getRequestURI())
        );
    }

    @GetMapping("/fields/{id}")
    public ResponseEntity<FieldResponse> getFieldById(@PathVariable Integer id) {
        return ResponseEntity.ok(fieldService.getFieldById(id));
    }

    @PostMapping("/fields")
    public ResponseEntity<ApiResponse<FieldResponse>> createField(
            @Valid @RequestBody FieldRequest req,
            HttpServletRequest request) {
        FieldResponse res = fieldService.createField(req);
        return ResponseEntity.ok(ApiResponse.ok(res, "Field created successfully", request.getRequestURI()));
    }

    @PutMapping("fields/{id}")
    public ResponseEntity<ApiResponse<FieldResponse>> updateField(@PathVariable int id,
            @RequestBody FieldUpdateRequest req, HttpServletRequest request) {
        FieldResponse updated = fieldService.updateField(id, req);
        return ResponseEntity.ok(
                ApiResponse.ok(updated, "Field updated successfully", request.getRequestURI()));
    }

    @DeleteMapping("/fields/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteField(
            @PathVariable int id, HttpServletRequest request) {
        fieldService.deleteField(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Field deleted successfully", request.getRequestURI()));
    }
}
