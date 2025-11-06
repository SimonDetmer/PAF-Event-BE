package org.example.eventm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http
            .csrf(csrf -> csrf.disable())  // For development only, enable in production
            .authorizeHttpRequests(auth -> auth
                // Allow all static resources
                .requestMatchers(
                    "/",
                    "/error",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // Allow all API endpoints (for development)
                .requestMatchers("/api/**").permitAll()
                // Require authentication for everything else
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                corsConfig.setAllowedOrigins(java.util.List.of("*"));
                corsConfig.setAllowedMethods(java.util.List.of("*"));
                corsConfig.setAllowedHeaders(java.util.List.of("*"));
                return corsConfig;
            }));

        return http.build();
    }
}
