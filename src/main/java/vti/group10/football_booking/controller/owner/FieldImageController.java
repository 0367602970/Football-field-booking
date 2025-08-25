package vti.group10.football_booking.controller.owner;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.ApiResponse;
import vti.group10.football_booking.service.owner.FieldImageService;

@Controller
@RequestMapping("/api/owner/fields")
@RequiredArgsConstructor
public class FieldImageController {
    private final FieldImageService imageService;

    // Thêm nhiều ảnh cho sân
    @PostMapping("/{fieldId}/images")
    public ResponseEntity<ApiResponse<List<String>>> addImages(
            @PathVariable Long fieldId,
            @RequestBody List<String> imageUrls,
            HttpServletRequest request) {

        List<String> urls = imageService.addImages(fieldId, imageUrls);
        return ResponseEntity.ok(
                ApiResponse.ok(urls, "Images added successfully", request.getRequestURI())
        );
    }

    // Xoá ảnh khỏi sân
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<ApiResponse<Object>> deleteImage(
            @PathVariable Long imageId,
            HttpServletRequest request) {
        imageService.deleteImage(imageId);
        return ResponseEntity.ok(
                ApiResponse.ok(null, "Image deleted successfully", request.getRequestURI())
        );
    }

    // Lấy danh sách ảnh của sân
    @GetMapping("/{fieldId}/images")
    public ResponseEntity<ApiResponse<List<String>>> getImages(
            @PathVariable Long fieldId,
            HttpServletRequest request) {
        List<String> urls = imageService.getImages(fieldId);
        return ResponseEntity.ok(
                ApiResponse.ok(urls, "List of images", request.getRequestURI())
        );
    }
}
