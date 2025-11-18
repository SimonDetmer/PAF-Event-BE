package org.example.eventm.api.controller;

import org.example.eventm.api.service.AuthService;
import org.example.eventm.api.service.EmailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    public AuthController(AuthService authService, EmailService emailService) {
        this.authService = authService;
        this.emailService = emailService;
    }

    @PostMapping("/request-login")
    public String requestLogin(@RequestParam String email) {

        String token = authService.requestLogin(email);

        emailService.sendLoginEmail(email, token);

        return "Login-E-Mail gesendet.";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String token) {
        return authService.verifyToken(token);
    }
}
