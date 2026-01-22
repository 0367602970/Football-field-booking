package vti.group10.football_booking.controller.owner;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.ApiResponse;
import vti.group10.football_booking.dto.response.FieldImageResponse;
import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.service.owner.FieldImageService;

@Controller
@RequestMapping("/api/owner/fields")
@RequiredArgsConstructor
public class FieldImageController {
    private final FieldImageService fieldImageService;

    // Upload 1 ảnh
    @PostMapping("/{fieldId}/images")
    public ResponseEntity<ApiResponse<FieldImageResponse>> uploadImage(
            @PathVariable int fieldId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {

        FieldImage img = fieldImageService.addImage(fieldId, file);

        FieldImageResponse response = new FieldImageResponse(img.getId(), img.getImageUrl());
        return ResponseEntity.ok(ApiResponse.ok(
                response,
                "Image uploaded successfully",
                request.getRequestURI()));
    }

    // Lấy danh sách ảnh
    @GetMapping("/{fieldId}/images")
    public ResponseEntity<ApiResponse<List<FieldImage>>> getImages(
            @PathVariable int fieldId,
            HttpServletRequest request) {

        List<FieldImage> images = fieldImageService.getImages(fieldId);
        return ResponseEntity.ok(ApiResponse.ok(
                images,
                "Images retrieved successfully",
                request.getRequestURI()));
    }

    // Xóa ảnh
    @DeleteMapping("/{fieldId}/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @PathVariable int fieldId,
            @PathVariable int imageId,
            HttpServletRequest request) {

        fieldImageService.deleteImage(imageId);

        return ResponseEntity.ok(ApiResponse.ok(
                null,
                "Image deleted successfully",
                request.getRequestURI()));
    }
}
