package org.example.eventm.api.service;

import org.example.eventm.api.model.LoginToken;
import org.example.eventm.api.model.User;
import org.example.eventm.api.repository.LoginTokenRepository;
import org.example.eventm.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final LoginTokenRepository loginTokenRepository;
    private final TokenGenerator tokenGenerator;

    public AuthService(UserRepository userRepository,
                       LoginTokenRepository loginTokenRepository,
                       TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.loginTokenRepository = loginTokenRepository;
        this.tokenGenerator = tokenGenerator;
    }

    public String requestLogin(String email) {

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        LoginToken loginToken = new LoginToken(token, email, expiresAt);
        loginTokenRepository.save(loginToken);

        return token;
    }

    public String verifyToken(String token) {

        Optional<LoginToken> opt = loginTokenRepository.findByToken(token);
        if (opt.isEmpty()) {
            throw new RuntimeException("Ungültiger Token");
        }

        LoginToken loginToken = opt.get();

        if (loginToken.isExpired()) {
            throw new RuntimeException("Token ist abgelaufen");
        }

        User user = userRepository
                .findByEmail(loginToken.getEmail())
                .orElseGet(() -> userRepository.save(new User(loginToken.getEmail())));

        return tokenGenerator.generateJwt(user);
    }
}
