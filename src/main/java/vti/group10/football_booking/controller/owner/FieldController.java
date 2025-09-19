package vti.group10.football_booking.controller.owner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import vti.group10.football_booking.config.security.CustomUserDetails;
import vti.group10.football_booking.dto.ApiResponse;
import vti.group10.football_booking.dto.request.FieldRequest;
import vti.group10.football_booking.dto.request.FieldUpdateRequest;
import vti.group10.football_booking.dto.response.FieldResponse;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.service.UserService;
import vti.group10.football_booking.service.owner.FieldService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class FieldController {
    private final FieldService fieldService;
    private final UserService userService;
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
    @PostMapping(value = "/fields", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<FieldResponse>> createField(
            @RequestPart("field") String fieldJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request) throws IOException, ServletException {

        // Parse JSON string thành FieldRequest
        for (Part p : request.getParts()) {
            System.out.println("Part: " + p.getName() + ", content-type=" + p.getContentType() + ", size=" + p.getSize());
        }
        System.out.println("Received JSON: " + fieldJson);
        ObjectMapper mapper = new ObjectMapper();
        FieldRequest req = mapper.readValue(fieldJson, FieldRequest.class);

        User owner = userService.getById(userDetails.getId());

        FieldResponse res = fieldService.createField(req, owner, images);
        return ResponseEntity.ok(ApiResponse.ok(res, "Field created successfully", request.getRequestURI()));
    }


//    @PostMapping("/fields")
//    public ResponseEntity<ApiResponse<FieldResponse>> createField(
//            @Valid @RequestBody FieldRequest req,
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            HttpServletRequest request) {
//
//        User owner = userService.getById(userDetails.getId());
//
//        FieldResponse res = fieldService.createField(req, owner);
//        return ResponseEntity.ok(ApiResponse.ok(res, "Field created successfully", request.getRequestURI()));
//    }

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
