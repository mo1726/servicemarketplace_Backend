package com.example.service_marketplace.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Core hardening
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 401/403 behavior (optional: customize handlers if you want custom JSON)
                .exceptionHandling(eh -> {
                    // leave default for now
                })

                .authorizeHttpRequests(auth -> auth
                        // Preflight for CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public auth & static
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        // Current user profile (must be logged in)
                        .requestMatchers(HttpMethod.GET, "/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/me/avatar").authenticated()

                        // Admin-only user management
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        // Categories: public read, admin write
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")

                        // Services: public read, provider/admin write
                        .requestMatchers(HttpMethod.GET, "/services").permitAll()
                        .requestMatchers(HttpMethod.GET, "/services/*").permitAll()

                        // Ads list (public) — keep this open so user dashboard can show sponsored items
                        // If your ads endpoint is on AdRequestController as /ad-requests/ads, permit that too.
                        .requestMatchers(HttpMethod.GET, "/services/ads").permitAll()
                        .requestMatchers(HttpMethod.GET, "/ad-requests/ads").permitAll()

                        // Service images and other writes
                        .requestMatchers(HttpMethod.PUT, "/services/*/image").hasAnyRole("PROVIDER", "ADMIN")
                        .requestMatchers("/services/**").hasAnyRole("PROVIDER", "ADMIN")

                        // Reviews: public read, logged-in write (user or admin)
                        .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()
                        .requestMatchers("/reviews/**").hasAnyRole("USER", "ADMIN")

                        // Ad Requests (manual ads flow):
                        // Create and view own (provider). Prefer /ad-requests/mine (no providerId path) for IDOR safety.
                        .requestMatchers(HttpMethod.POST, "/ad-requests").hasRole("PROVIDER")
                        .requestMatchers(HttpMethod.GET, "/ad-requests/mine").hasRole("PROVIDER")

                        // Admin review/approval
                        .requestMatchers(HttpMethod.GET, "/ad-requests").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/ad-requests/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/ad-requests/*/activate").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/ad-requests/*/reject").hasRole("ADMIN")

                        // Everything else needs auth
                        .anyRequest().authenticated()
                )

                // JWT filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // tune strength if needed
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://your-frontend-domain.com"
        ));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
