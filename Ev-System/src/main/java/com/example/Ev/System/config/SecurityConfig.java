package com.example.Ev.System.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Permit preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Public
                        .requestMatchers("/", "/api/auth/**").permitAll()
                        // Role-based
//                        .requestMatchers("/api/appointments/**").permitAll() // ✅ public nho xoa
//                        .requestMatchers("/assignments/**").permitAll() // ✅ public nho xoa
//                        .requestMatchers("/MaintainanceRecord/**").permitAll() // ✅ public nho xoa
//                        .requestMatchers("/worklogs/**").permitAll() // ✅ public nho xoa
//                        .requestMatchers("/Users/**").permitAll() // ✅ public nho xoa
//                        .requestMatchers("/api/auth/register/**").permitAll() // ✅ public nho xoa

                        .requestMatchers("/api/admin/**").hasAuthority("admin")
                        .requestMatchers("/api/manager/**").hasAuthority("manager")
                        .requestMatchers("/api/staff/**").hasAuthority("staff")
                        .requestMatchers("/api/technician/**").hasAuthority("technician")
                        .requestMatchers("/api/customer/**").hasAuthority("customer")
                        // Các API còn lại chỉ cần authenticated
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ⚙️ Ghi rõ domain frontend (ở Vercel và local)
        configuration.setAllowedOrigins(Arrays.asList(
                "https://ev-teal.vercel.app",  //
                "http://localhost:5173",
                "https://ev-service-center-maintance-management-um2j.onrender.com" // cho phép chạy dev ở local
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Auth-Token"));
        configuration.setExposedHeaders(List.of("X-Auth-Token"));
        configuration.setAllowCredentials(true); // ✅ Cho phép gửi cookie/token qua request

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}