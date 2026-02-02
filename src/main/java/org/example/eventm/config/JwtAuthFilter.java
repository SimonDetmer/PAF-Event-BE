package org.example.eventm.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.eventm.api.repository.UserRepository;
import org.example.eventm.api.service.TokenGenerator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenGenerator tokenGenerator;
    private final UserRepository userRepository;

    public JwtAuthFilter(TokenGenerator tokenGenerator,
                         UserRepository userRepository) {
        this.tokenGenerator = tokenGenerator;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = tokenGenerator.extractEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userOpt = userRepository.findByEmail(email);

                if (userOpt.isPresent()) {
                    var user = userOpt.get();

                    var authToken = new UsernamePasswordAuthenticationToken(
                            email, // Principal (hier nur String)
                            null,
                            Collections.emptyList() // aktuell keine Rollen-Prüfung nötig
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
