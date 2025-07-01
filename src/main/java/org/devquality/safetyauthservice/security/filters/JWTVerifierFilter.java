package org.devquality.safetyauthservice.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devquality.safetyauthservice.security.service.UserDetailsServiceImpl;
import org.devquality.safetyauthservice.types.JWTType;
import org.devquality.safetyauthservice.utils.IJWTUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTVerifierFilter extends OncePerRequestFilter {

    private final IJWTUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService; // Especifica la implementación exacta

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        final String authHeader = request.getHeader("Authorization");

        // Skip JWT verification for public endpoints
        if (isPublicEndpoint(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = getJWTFromRequest(request);

        if (StringUtils.hasText(jwt) && jwtUtils.isTokenValid(jwt, JWTType.ACCESS_TOKEN)) {
            try {
                String username = jwtUtils.getSubjectFromToken(jwt, JWTType.ACCESS_TOKEN);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("✅ Successfully authenticated user: {}", username);
                }
            } catch (Exception e) {
                log.error("❌ Error processing JWT token: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else if (StringUtils.hasText(jwt)) {
            log.debug("⚠️ Invalid or expired JWT token");
        }

        filterChain.doFilter(request, response);
    }

    private String getJWTFromRequest(HttpServletRequest request) {
        final String bearerStart = "Bearer ";
        final String bearerToken = request.getHeader("Authorization");

        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith(bearerStart)) {
            return null;
        }

        return bearerToken.substring(bearerStart.length());
    }

    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.startsWith("/api/v1/auth/") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/actuator/health") ||
                requestURI.equals("/") ||
                requestURI.equals("/favicon.ico");
    }
}