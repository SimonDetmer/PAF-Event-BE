package org.example.eventm.api.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.eventm.api.dto.CreateUserRequest;
import org.example.eventm.api.model.User;
import org.example.eventm.api.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ------------------------------------------------------------
    //  GET /api/users  – Liste aller User (vereinfachtes DTO)
    // ------------------------------------------------------------
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        log.info("Fetching all users");
        try {
            List<User> users = userRepository.findAll();
            log.info("Found {} users", users.size());

            List<Map<String, Object>> userDtos = users.stream()
                    .map(user -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("id", user.getId());
                        dto.put("email", user.getEmail());
                        dto.put("name", user.getName());
                        dto.put("role", user.getRole());
                        dto.put("createdAt", user.getCreatedAt());
                        return dto;
                    })
                    .toList();

            return ResponseEntity.ok(userDtos);
        } catch (Exception e) {
            log.error("Error fetching users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error fetching users",
                            "message", e.getMessage(),
                            "type", e.getClass().getSimpleName()
                    ));
        }
    }

    // ------------------------------------------------------------
    //  GET /api/users/me – aktuell eingeloggter User
    //  -> wird vom JWT (JwtAuthFilter) + Authentication gespeist
    // ------------------------------------------------------------
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not authenticated"));
        }

        String email = authentication.getName(); // kommt aus JwtAuthFilter

        return userRepository.findByEmail(email)
                .map(user -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", user.getId());
                    dto.put("email", user.getEmail());
                    dto.put("name", user.getName());
                    dto.put("role", user.getRole());
                    dto.put("createdAt", user.getCreatedAt());
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found: " + email)));
    }

    // ------------------------------------------------------------
    //  POST /api/users – Registrierung
    //  Body: { "email": "...", "role": "eventmanager" | "customer", "name": "optional" }
    // ------------------------------------------------------------
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        log.info("Creating user with email: {}", createUserRequest.getEmail());
        try {
            String email = createUserRequest.getEmail();
            String role = createUserRequest.getRole();
            String name = createUserRequest.getName();

            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Email is required"));
            }

            if (role == null || role.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Role is required"));
            }

            // optional: Role validieren
            if (!role.equals("eventmanager") && !role.equals("customer")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Invalid role: " + role));
            }

            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                log.info("User with email {} already exists, returning existing user", email);
                User existing = existingUser.get();

                Map<String, Object> response = new HashMap<>();
                response.put("id", existing.getId());
                response.put("email", existing.getEmail());
                response.put("name", existing.getName());
                response.put("role", existing.getRole());
                response.put("createdAt", existing.getCreatedAt());
                response.put("message", "User already exists");

                return ResponseEntity.ok(response);
            }

            // Neuen User anlegen
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setRole(role);
            newUser.setCreatedAt(LocalDateTime.now());
            if (name != null && !name.isBlank()) {
                newUser.setName(name);
            }

            User savedUser = userRepository.save(newUser);
            log.info("Created new user with ID: {}", savedUser.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedUser.getId());
            response.put("email", savedUser.getEmail());
            response.put("name", savedUser.getName());
            response.put("role", savedUser.getRole());
            response.put("createdAt", savedUser.getCreatedAt());
            response.put("message", "User created successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error creating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error creating user",
                            "message", e.getMessage(),
                            "type", e.getClass().getSimpleName()
                    ));
        }
    }

    // ------------------------------------------------------------
    //  GET /api/users/email/{email}
    // ------------------------------------------------------------
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        log.info("Received request for email: {}", email);
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            log.info("User found: {}", userOpt.isPresent());

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                log.info("User details - ID: {}, Email: {}", user.getId(), user.getEmail());

                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("email", user.getEmail());
                response.put("name", user.getName());
                response.put("role", user.getRole());
                response.put("createdAt", user.getCreatedAt());

                return ResponseEntity.ok(response);
            } else {
                log.warn("No user found with email: {}", email);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error in getUserByEmail", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    // ------------------------------------------------------------
    //  GET /api/users/{id}
    // ------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", user.getId());
                    dto.put("email", user.getEmail());
                    dto.put("name", user.getName());
                    dto.put("role", user.getRole());
                    dto.put("createdAt", user.getCreatedAt());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------------------------------------------------
    //  PUT /api/users/{id}
    //  (einfach gehalten – Email + Name + Role aktualisierbar)
    // ------------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @Valid @RequestBody User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setName(updatedUser.getName());
                    if (updatedUser.getRole() != null && !updatedUser.getRole().isBlank()) {
                        existingUser.setRole(updatedUser.getRole());
                    }
                    User savedUser = userRepository.save(existingUser);

                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", savedUser.getId());
                    dto.put("email", savedUser.getEmail());
                    dto.put("name", savedUser.getName());
                    dto.put("role", savedUser.getRole());
                    dto.put("createdAt", savedUser.getCreatedAt());

                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------------------------------------------------
    //  DELETE /api/users/{id}
    // ------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
