package org.example.eventm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(cs -> cs.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration cfg = new CorsConfiguration();
                    cfg.setAllowedOrigins(List.of("http://localhost:4200"));
                    cfg.setAllowCredentials(true);

                    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    cfg.setAllowedHeaders(List.of(
                            "Authorization",
                            "Content-Type",
                            "X-Requested-With",
                            "Accept"
                    ));

                    cfg.setExposedHeaders(List.of("Authorization"));
                    return cfg;
                }))

                .authorizeHttpRequests(auth -> auth
                        // Swagger
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // FE → BE API
                        .requestMatchers("/api/**").permitAll()

                        // alles andere blockieren
                        .anyRequest().permitAll()
                )

                .formLogin(f -> f.disable())
                .httpBasic(h -> h.disable());

        return http.build();
    }
}
