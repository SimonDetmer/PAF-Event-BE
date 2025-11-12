package org.example.eventm.api.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.eventm.api.dto.CreateUserRequest;
import org.example.eventm.api.model.User;
import org.example.eventm.api.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        log.info("Fetching all users");
        try {
            List<User> users = userRepository.findAll();
            log.info("Found {} users", users.size());
            
            // Create a list of user DTOs to avoid circular references
            List<Map<String, Object>> userDtos = users.stream()
                .map(user -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", user.getId());
                    dto.put("email", user.getEmail());
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

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        log.info("Creating user with email: {}", createUserRequest.getEmail());
        try {
            // Check if user already exists
            Optional<User> existingUser = userRepository.findByEmail(createUserRequest.getEmail());
            if (existingUser.isPresent()) {
                log.info("User with email {} already exists, returning existing user", createUserRequest.getEmail());
                User existing = existingUser.get();
                Map<String, Object> response = new HashMap<>();
                response.put("id", existing.getId());
                response.put("email", existing.getEmail());
                response.put("message", "User already exists");
                return ResponseEntity.ok(response);
            }
            
            // Create new user
            User newUser = new User();
            newUser.setEmail(createUserRequest.getEmail());
            newUser.setCreatedAt(LocalDateTime.now());
            
            User savedUser = userRepository.save(newUser);
            log.info("Created new user with ID: {}", savedUser.getId());
            
            // Return response with created user (excluding sensitive/relationship data)
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedUser.getId());
            response.put("email", savedUser.getEmail());
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

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        log.info("Received request for email: {}", email);
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            log.info("User found: {}", userOpt.isPresent());
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                log.info("User details - ID: {}, Email: {}", user.getId(), user.getEmail());
                
                // Create a simple DTO to avoid circular references
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("email", user.getEmail());
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

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setEmail(updatedUser.getEmail());
                    User savedUser = userRepository.save(existingUser);
                    return ResponseEntity.ok(savedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
