package vti.group10.football_booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.request.LoginRequest;
import vti.group10.football_booking.dto.request.RegisterRequest;
import vti.group10.football_booking.dto.response.AuthResponse;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
        }

        User.Role role = "OWNER".equalsIgnoreCase(req.getRole()) ? User.Role.OWNER : User.Role.USER;

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        // Tạo access + refresh token
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Lưu refresh token vào DB
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600)
                .user(Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "role", user.getRole().name()
                ))
                .build();
    } 


    public AuthResponse login(LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600)
                .user(Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "role", user.getRole().name()
                ))
                .build();
    }

    public AuthResponse refresh(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // update DB
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(3600)
                .user(Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "role", user.getRole().name()
                ))
                .build();
    }

    private AuthResponse createTokens(User user, Authentication authentication) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600)
                .user(Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "role", user.getRole().name()
                ))
                .build();
    }

    public Map<String, Object> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user");
        }

        Object principal = authentication.getPrincipal();
        System.out.println("Authentication principal: " + principal);
        if (principal instanceof UserDetails userDetails) {
            // load thêm từ DB nếu cần
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found 1"));

            return Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "fullName", user.getFullName(),
                    "role", user.getRole().name()
            );
        } else if (principal instanceof String email) {
            // nếu principal chỉ là email (String)
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found 2"));

            return Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "fullName", user.getFullName(),
                    "role", user.getRole().name()
            );
        }

        throw new RuntimeException("Invalid authentication principal");
    }
}
