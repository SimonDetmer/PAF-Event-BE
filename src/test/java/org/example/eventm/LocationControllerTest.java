package org.example.eventm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eventm.api.controller.LocationController;
import org.example.eventm.api.model.Location;
import org.example.eventm.api.repository.LocationRepository;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class LocationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationController locationController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Location location;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(locationController).build();
        location = new Location();
        location.setId(1);
    }

    @Test
    void testGetAllLocations() throws Exception {
        when(locationRepository.findAll()).thenReturn(Arrays.asList(location));

        mockMvc.perform(get("/locations"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetLocationById() throws Exception {
        when(locationRepository.findById(1)).thenReturn(Optional.of(location));

        mockMvc.perform(get("/locations/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateLocation() throws Exception {
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        mockMvc.perform(post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(location)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteLocation() throws Exception {
        when(locationRepository.existsById(1)).thenReturn(true);
        Mockito.doNothing().when(locationRepository).deleteById(1);

        mockMvc.perform(delete("/locations/1"))
                .andExpect(status().isNoContent());
    }
}
