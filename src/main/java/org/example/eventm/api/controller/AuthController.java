package org.example.eventm.api.controller;

import org.example.eventm.api.model.User;
import org.example.eventm.api.repository.UserRepository;
import org.example.eventm.api.service.TokenGenerator;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UserRepository userRepository;
    private final TokenGenerator tokenGenerator;

    public AuthController(UserRepository userRepository,
                          TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.tokenGenerator = tokenGenerator;
    }

    @PostMapping("/login-simple")
    public Map<String, Object> loginSimple(@RequestParam String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        String jwt = tokenGenerator.generateJwt(user);

        Map<String, Object> response = new HashMap<>();
        response.put("jwt", jwt);
        response.put("user", user);

        return response;
    }
}
