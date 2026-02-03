package org.example.eventm.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateUserRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String role;   // "eventmanager" oder "customer"

    private String name;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String email, String role, String name) {
        this.email = email;
        this.role = role;
        this.name = name;
    }

    // ===== Getter & Setter =====

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
