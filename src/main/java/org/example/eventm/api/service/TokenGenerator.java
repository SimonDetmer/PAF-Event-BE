package org.example.eventm.api.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.eventm.api.model.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenGenerator {

    // 256-bit Base64 Key
    private static final String SECRET_KEY =
            "MzJ5ZHNmbGprbHNkZmprbHNkZmtqbmZkc2xrM2poc2prZmQ=";

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public String generateToken(String email) {
        return buildJwt(email);
    }

    public String generateJwt(User user) {
        return buildJwt(user.getEmail());
    }

    private String buildJwt(String email) {

        long now = System.currentTimeMillis();
        long expirationMs = 1000 * 60 * 60 * 24; // 24h

        return Jwts.builder()
                .setSubject(email)                     // <-- richtig in 0.11
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(getKey(), SignatureAlgorithm.HS256) // <-- richtig in 0.11
                .compact();
    }
}
