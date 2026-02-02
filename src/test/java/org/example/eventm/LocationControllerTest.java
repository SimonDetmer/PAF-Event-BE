package org.example.eventm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eventm.api.model.Location;
import org.example.eventm.api.repository.LocationRepository;
import org.example.eventm.api.controller.LocationController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.math.BigInteger;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class LocationControllerTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationController locationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(locationController).build();
        objectMapper = new ObjectMapper();
    }

    private Location createLocation() {
        Location loc = new Location();
        loc.setId(1);
        loc.setName("Stadthalle");
        loc.setCity("Konstanz");
        loc.setCapacity(BigInteger.valueOf(500));
        return loc;
    }

    @Test
    void getAllLocations_returnsList() throws Exception {
        Location loc = createLocation();
        when(locationRepository.findAll()).thenReturn(List.of(loc));

        mockMvc.perform(get("/api/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(loc.getId())))
                .andExpect(jsonPath("$[0].name", is(loc.getName())))
                .andExpect(jsonPath("$[0].city", is(loc.getCity())));
    }

    @Test
    void createLocation_persistsAndReturnsLocation() throws Exception {
        Location loc = createLocation();
        loc.setId(null); // ID wird von DB vergeben

        Location saved = createLocation(); // mit ID 1
        when(locationRepository.save(any(Location.class))).thenReturn(saved);

        mockMvc.perform(
                        post("/api/locations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loc))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId())))
                .andExpect(jsonPath("$.name", is(saved.getName())))
                .andExpect(jsonPath("$.city", is(saved.getCity())));
    }

    @Test
    void getLocationById_existing_returnsLocation() throws Exception {
        Location loc = createLocation();
        when(locationRepository.findById(1)).thenReturn(Optional.of(loc));

        mockMvc.perform(get("/api/locations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(loc.getId())))
                .andExpect(jsonPath("$.name", is(loc.getName())));
    }

    @Test
    void getLocationById_notExisting_returns404() throws Exception {
        when(locationRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/locations/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateLocation_existing_updatesAndReturns() throws Exception {
        Location existing = createLocation();
        Location update = createLocation();
        update.setName("Neuer Name");
        update.setCity("Neue Stadt");

        when(locationRepository.findById(1)).thenReturn(Optional.of(existing));
        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(
                        put("/api/locations/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(update))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Neuer Name")))
                .andExpect(jsonPath("$.city", is("Neue Stadt")));
    }

    @Test
    void deleteLocation_existing_returns204() throws Exception {
        when(locationRepository.existsById(1)).thenReturn(true);
        doNothing().when(locationRepository).deleteById(1);

        mockMvc.perform(delete("/api/locations/1"))
                .andExpect(status().isNoContent());

        verify(locationRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteLocation_notExisting_returns404() throws Exception {
        when(locationRepository.existsById(1)).thenReturn(false);

        mockMvc.perform(delete("/api/locations/1"))
                .andExpect(status().isNotFound());

        verify(locationRepository, never()).deleteById(1);
    }
}
