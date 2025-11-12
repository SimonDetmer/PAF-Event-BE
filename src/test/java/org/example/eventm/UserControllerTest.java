package org.example.eventm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eventm.api.controller.UserController;
import org.example.eventm.api.model.User;
import org.example.eventm.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)  // Neue Alternative zu @MockBean
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON Unterstützung

    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));
    }

    @Test
    void testGetUserById() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        String userJson = "{\"email\":\"john@example.com\"}";
        
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.message").value("User created successfully"));
    }

    @Test
    void testDeleteUser() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(userRepository).deleteById(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        when(userRepository.existsById(2L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/2"))
                .andExpect(status().isNotFound());
    }
}
