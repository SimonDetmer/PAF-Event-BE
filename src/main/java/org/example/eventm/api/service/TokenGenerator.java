package org.example.eventm.api.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.eventm.api.model.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenGenerator {

    private final String secret = "MY_SECRET_KEY_123";

    public String generateJwt(User user) {

        long now = System.currentTimeMillis();
        long expiry = now + (1000 * 60 * 30); // 30 min

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expiry))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}
