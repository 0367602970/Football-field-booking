package vti.group10.football_booking.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import vti.group10.football_booking.dto.ApiResponse;
import vti.group10.football_booking.dto.request.LoginRequest;
import vti.group10.football_booking.dto.request.RegisterRequest;
import vti.group10.football_booking.dto.response.AuthResponse;
import vti.group10.football_booking.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Đăng ký
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);

        // Gắn cookies
        addAuthCookies(response, authResponse);

        return ResponseEntity.ok(Map.of(
                "message", "Register success",
                "user", authResponse.getUser()
        ));
    }

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);

        // Gắn cookies
        addAuthCookies(response, authResponse);

        return ResponseEntity.ok(Map.of(
                "message", "Login success",
                "user", authResponse.getUser()
        ));
    }

    // Refresh token
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookieValue(request, "refreshToken");
        if (refreshToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token not found"));
        }

        AuthResponse authResponse = authService.refresh(refreshToken);

        // Cập nhật cookies
        addAuthCookies(response, authResponse);

        return ResponseEntity.ok(Map.of("message", "Token refreshed"));
    }

    // Lấy thông tin user hiện tại
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    // Đăng xuất (xóa cookies)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Xóa cookie bằng cách set MaxAge = 0
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true).secure(true).path("/").maxAge(0).sameSite("Strict").build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true).secure(true).path("/").maxAge(0).sameSite("Strict").build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("message", "Logged out"));
    }

    private void addAuthCookies(HttpServletResponse response, AuthResponse authResponse) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", authResponse.getAccessToken())
                .httpOnly(true).secure(true).path("/")
                .maxAge(authResponse.getExpiresIn()) // ví dụ 3600
                .sameSite("None").build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                .httpOnly(true).secure(true).path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 ngày
                .sameSite("None").build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
    
    
}
