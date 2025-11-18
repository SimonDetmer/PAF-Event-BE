package org.example.eventm.config;

import org.example.eventm.config.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Wir verwenden JWT → keine Server-Session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // CSRF brauchen wir bei reiner REST-API nicht
                .csrf(csrf -> csrf.disable())

                // CORS für Angular-Frontend
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

                // Zugriffregeln
                .authorizeHttpRequests(auth -> auth
                        // Login offen
                        .requestMatchers("/auth/**").permitAll()

                        // Swagger vollständig freigeben
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/webjars/**"
                        ).permitAll()

                        // Public GET APIs
                        .requestMatchers(HttpMethod.GET,
                                "/api/events/**",
                                "/api/locations/**"
                        ).permitAll()

                        // Private APIs ⇒ JWT
                        .requestMatchers(
                                "/api/orders/**",
                                "/api/tickets/**",
                                "/api/reports/**",
                                "/api/users/**"
                        ).authenticated()

                        .anyRequest().authenticated()
                )


                // Unser JWT-Filter vor UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Form-Login & HTTP Basic deaktivieren (nur JWT)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
