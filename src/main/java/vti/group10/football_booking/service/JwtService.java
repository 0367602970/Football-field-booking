package vti.group10.football_booking.service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import vti.group10.football_booking.model.User;

@Service
public class JwtService {
    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.access-expiration-seconds}")
    private long accessTokenValidity;

    @Value("${security.jwt.refresh-expiration-seconds}")
    private long refreshTokenValidity;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("username", user.getEmail())
                .claim("roles", List.of("ROLE_" + user.getRole().name()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenValidity)))
                .signWith(key())
                .compact();
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTokenValidity)))
                .signWith(key())
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token);
    }
}
