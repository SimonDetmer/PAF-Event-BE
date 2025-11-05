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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
        
        // Initialize a valid Location object with required fields
        location = new Location();
        location.setId(1);
        location.setStreet("Test Street");
        location.setGeoX(new BigDecimal("0.0"));
        location.setGeoY(new BigDecimal("0.0"));
        location.setCapacity(BigInteger.valueOf(100));
    }

    @Test
    void testGetAllLocations() throws Exception {
        // Given
        List<Location> locations = Arrays.asList(location);
        when(locationRepository.findAll()).thenReturn(locations);

        // When/Then
        mockMvc.perform(get("/api/locations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(location.getId())));

        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void testGetLocationById() throws Exception {
        // Given
        when(locationRepository.findById(1)).thenReturn(Optional.of(location));

        // When/Then
        mockMvc.perform(get("/api/locations/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(location.getId())));

        verify(locationRepository, times(1)).findById(1);
    }

    @Test
    void testCreateLocation() throws Exception {
        // Given
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        // When/Then
        mockMvc.perform(post("/api/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(location)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(location.getId())));

        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void testDeleteLocation() throws Exception {
        // Given
        when(locationRepository.existsById(1)).thenReturn(true);
        doNothing().when(locationRepository).deleteById(1);

        // When/Then
        mockMvc.perform(delete("/api/locations/1"))
                .andExpect(status().isNoContent());

        verify(locationRepository, times(1)).existsById(1);
        verify(locationRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteLocation_NotFound() throws Exception {
        // Given
        when(locationRepository.existsById(999)).thenReturn(false);

        // When/Then
        mockMvc.perform(delete("/api/locations/999"))
                .andExpect(status().isNotFound());

        verify(locationRepository, times(1)).existsById(999);
        verify(locationRepository, never()).deleteById(anyInt());
    }
}
