package org.devquality.safetyauthservice.security.config;

import lombok.RequiredArgsConstructor;
import org.devquality.safetyauthservice.persistence.enums.UserRole;
import org.devquality.safetyauthservice.security.exceptions.ExceptionAccessDeniedHandlerImpl;
import org.devquality.safetyauthservice.security.exceptions.ExceptionAuthenticationEntryPointImpl;
import org.devquality.safetyauthservice.security.filters.JWTVerifierFilter;
import org.devquality.safetyauthservice.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTVerifierFilter jwtVerifierFilter;
    private final UserDetailsServiceImpl userDetailsService;
    private final ExceptionAccessDeniedHandlerImpl accessDeniedHandler;
    private final ExceptionAuthenticationEntryPointImpl authenticationEntryPoint;

    @Value("${app.security.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.security.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${app.security.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${app.security.cors.exposed-headers}")
    private String exposedHeaders;

    @Value("${app.security.cors.allow-credentials}")
    private boolean allowCredentials;

    @Value("${app.security.cors.max-age}")
    private long maxAge;

    // Constantes para roles
    private static final String ROLE_ADMIN = "ROLE_" + UserRole.ADMIN.name();
    private static final String ROLE_MODERATOR = "ROLE_" + UserRole.MODERATOR.name();
    private static final String ROLE_CITIZEN = "ROLE_" + UserRole.CITIZEN.name();

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtVerifierFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(authenticationEntryPoint);
                    exception.accessDeniedHandler(accessDeniedHandler);
                })
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/actuator/health",
                                "/actuator/info",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()

                        // Auth endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh-token").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/health").permitAll()

                        // Admin endpoints - Solo ADMIN
                        .requestMatchers("/api/v1/admin/**").hasAuthority(ROLE_ADMIN)

                        // Moderator endpoints - MODERATOR y ADMIN
                        .requestMatchers("/api/v1/moderator/**").hasAnyAuthority(ROLE_MODERATOR, ROLE_ADMIN)

                        // User management endpoints con restricciones específicas
                        .requestMatchers(HttpMethod.GET, "/api/v1/users").hasAuthority(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{id}").hasAnyAuthority(ROLE_MODERATOR, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/{id}/activate").hasAuthority(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/{id}/deactivate").hasAuthority(ROLE_ADMIN)

                        // User profile endpoints
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/profile").authenticated()
                        .requestMatchers("/api/v1/auth/me", "/api/v1/auth/logout").authenticated()

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Increased strength
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false); // Para debugging en dev
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Parse allowed origins
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        configuration.setExposedHeaders(Arrays.asList(exposedHeaders.split(",")));
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}