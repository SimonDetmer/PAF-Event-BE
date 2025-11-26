package org.example.eventm.api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class TokenGenerator {

    // Für Demo ok, für Produktion: in Config auslagern & langes Secret!
    private static final String SECRET_KEY =
            "CHANGE_ME_TO_A_SECURE_SECRET_CHANGE_ME_TO_A_SECURE_SECRET";

    private Key getSigningKey() {
        // Base64-Encoded String → Key
        byte[] keyBytes = Base64.getEncoder().encode(SECRET_KEY.getBytes());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Erzeugt ein JWT mit der E-Mail als Subject.
     */
    public String generateToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 1000L * 60 * 60 * 24); // 24h

        return Jwts.builder()
                .setSubject(email)                        // <--- korrekt für 0.11.x
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Email wieder aus dem Token lesen.
     */
    public String extractEmail(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())           // <--- korrekt für 0.11.x
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
