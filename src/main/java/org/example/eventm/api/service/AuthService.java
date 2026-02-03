package org.example.eventm.api.service;

import org.example.eventm.api.model.User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final TokenGenerator tokenGenerator;

    public AuthService(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    public String generateTokenForUser(User user) {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User or email must not be null");
        }
        return tokenGenerator.generateToken(user.getEmail());
    }
}
