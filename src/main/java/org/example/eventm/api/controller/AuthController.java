package org.example.eventm.api.controller;

import org.example.eventm.api.model.User;
import org.example.eventm.api.repository.UserRepository;
import org.example.eventm.api.service.TokenGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UserRepository userRepository;
    private final TokenGenerator tokenGenerator;

    public AuthController(UserRepository userRepository, TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.tokenGenerator = tokenGenerator;
    }

    // ------------------------------------------------------------
    //  POST /auth/register?email=...&role=...
    //  Wird von CreateUserComponent aufgerufen
    // ------------------------------------------------------------
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam String email,
            @RequestParam String role,
            @RequestParam(required = false) String name
    ) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email is required"));
        }

        if (role == null || role.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Role is required"));
        }

        if (!role.equals("eventmanager") && !role.equals("customer")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid role: " + role));
        }

        // Falls User schon existiert → einfach bestehenden zurückgeben
        return userRepository.findByEmail(email)
                .map(existing -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", existing.getId());
                    response.put("email", existing.getEmail());
                    response.put("name", existing.getName());
                    response.put("role", existing.getRole());
                    response.put("createdAt", existing.getCreatedAt());
                    response.put("message", "User already exists");
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(email);
                    user.setRole(role);
                    if (name != null && !name.isBlank()) {
                        user.setName(name);
                    }

                    User saved = userRepository.save(user);

                    Map<String, Object> response = new HashMap<>();
                    response.put("id", saved.getId());
                    response.put("email", saved.getEmail());
                    response.put("name", saved.getName());
                    response.put("role", saved.getRole());
                    response.put("createdAt", saved.getCreatedAt());
                    response.put("message", "User created successfully");

                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                });
    }

    // ------------------------------------------------------------
    //  POST /auth/login-simple?email=...
    // ------------------------------------------------------------
    @PostMapping("/login-simple")
    public ResponseEntity<Map<String, Object>> loginSimple(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "User not found: " + email));

        String jwt = tokenGenerator.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("jwt", jwt);
        response.put("user", user);

        return ResponseEntity.ok(response);
    }
}

