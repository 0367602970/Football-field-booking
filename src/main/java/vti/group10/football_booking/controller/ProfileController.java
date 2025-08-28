package vti.group10.football_booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.request.UpdateProfileRequest;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.service.ProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PutMapping("/update")
    public ResponseEntity<User> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User updatedUser = profileService.updateProfile(request);
        return ResponseEntity.ok(updatedUser);
    }
}
