package vti.group10.football_booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.request.UpdateProfileRequest;
import vti.group10.football_booking.dto.response.ProfileResponse;
import vti.group10.football_booking.service.ProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Hiển thị thông tin cá nhân
     */
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile() {
        ProfileResponse profile = profileService.getProfile();
        return ResponseEntity.ok(profile);
    }

    /**
     * Cập nhật thông tin cá nhân
     */
    @PutMapping("/update")
    public ResponseEntity<ProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        ProfileResponse updatedProfile = profileService.updateProfile(request);
        return ResponseEntity.ok(updatedProfile);
    }
}
