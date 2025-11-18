package org.example.eventm.api.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendLoginEmail(String email, String token) {
        System.out.println("=== LOGIN E-MAIL ===");
        System.out.println("An: " + email);
        System.out.println("Link: http://localhost:4200/login?token=" + token);
        System.out.println("====================");
    }
}
