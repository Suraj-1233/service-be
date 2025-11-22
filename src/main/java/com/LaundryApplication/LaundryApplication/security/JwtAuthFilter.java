package com.LaundryApplication.LaundryApplication.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.LaundryApplication.LaundryApplication.repository.UserRepository;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
      @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                // ✅ Validate token
                if (jwtUtil.validateToken(token)) {
                    String email = jwtUtil.getEmailFromToken(token);
                    String role = jwtUtil.getRoleFromToken(token);
                    String userId = jwtUtil.extractUserId(token);

                    // 🛑 CHECK USER EXISTS & ACTIVE
                    var userOpt = userRepository.findByEmail(email);
                    if (userOpt.isEmpty() || !userOpt.get().isActive()) {
                        System.out.println("❌ USER_NOT_FOUND/Inactive");

                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("""
                            {"success":false,"message":"USER_NOT_FOUND"}
                        """);
                        return;
                    }

                    if (role == null || role.isEmpty()) {
                        role = "USER"; // Default role
                    }

                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                    // ✅ Create Authentication object
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(email, null, authorities);

                    // ✅ (Optional) Add userId to details for later use in controllers
                    authentication.setDetails(userId);

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Log authentication info
                    System.out.println("✅ JWT Authenticated: " + email + " (role=" + role + ", userId=" + userId + ")");
                } else {
                    System.out.println("⚠️ Invalid JWT token detected");
                }
            }
        } catch (Exception ex) {
            System.err.println("❌ JWT Filter error: " + ex.getMessage());
            // Optional: clear context if token was invalid
            SecurityContextHolder.clearContext();
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}

