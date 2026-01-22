package vti.group10.football_booking.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vti.group10.football_booking.config.security.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import vti.group10.football_booking.service.JwtService;

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("🔥 JwtAuthFilter triggered for " + request.getRequestURI());
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        String token = null;
        System.out.println("Authorization header: " + request.getHeader("Authorization"));
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                System.out.println("Cookie: " + c.getName() + "=" + c.getValue());
            }
        }
        System.out.println("token from cookie: " + getTokenFromCookies(request));
        // 1. Lấy từ header
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        if (token == null) {
            token = getTokenFromCookies(request);
        }

        if (token != null) {
            try {
                var claims = jwtService.parseToken(token).getPayload();

                // Lấy thông tin từ claims
                Integer userId = Integer.valueOf(claims.getSubject());
                String username = claims.get("username", String.class);
                Object rolesObj = claims.get("roles");

                // Chuyển roles sang GrantedAuthority
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                if (rolesObj instanceof List) {
                    for (Object r : (List<?>) rolesObj) {
                        authorities.add(new SimpleGrantedAuthority(r.toString()));
                    }
                } else if (rolesObj instanceof String) {
                    authorities.add(new SimpleGrantedAuthority(rolesObj.toString()));
                }
                System.out.println("✅ User authenticated: " + username);
                System.out.println("✅ Authorities parsed from JWT: " + authorities);
                CustomUserDetails userDetails = new CustomUserDetails(userId, username, "", authorities);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("👉 SecurityContext set with principal=" + userDetails.getUsername()
                        + ", roles=" + userDetails.getAuthorities());

            } catch (JwtException e) {
                System.out.println("JWT error: " + e.getMessage());
            }
        }


        chain.doFilter(request, response);
    }


    private String getTokenFromCookies(HttpServletRequest request) {
    if (request.getCookies() != null) {
        for (Cookie cookie : request.getCookies()) {
            if ("accessToken".equals(cookie.getName())) { // tên cookie chứa token
                return cookie.getValue();
            }
        }
    }
    return null;
}

}
