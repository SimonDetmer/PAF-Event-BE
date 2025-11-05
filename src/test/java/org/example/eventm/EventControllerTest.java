package org.example.eventm;

import org.example.eventm.api.controller.EventController;
import org.example.eventm.api.model.Event;
import org.example.eventm.api.model.Location;
import org.example.eventm.api.repository.EventRepository;
import org.example.eventm.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventRepository eventRepository;
    
    @Mock
    private TicketService ticketService;

    @InjectMocks
    private EventController eventController;

    private Event event;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
            .setControllerAdvice()
            .build();
            
        // Create a minimal valid Location with required fields
        Location location = new Location();
        location.setId(1);
        location.setStreet("Test Street");
        location.setCapacity(BigInteger.valueOf(100));
        location.setGeoX(new BigDecimal("0.0"));
        location.setGeoY(new BigDecimal("0.0"));
        
        event = new Event();
        event.setId(1);
        event.setTitle("Test Event");
        event.setLocation(location);
        event.setEventDateTime(java.time.LocalDateTime.now().plusDays(1));
    }

    @Test
    void testGetAllEvents() throws Exception {
        // Given
        List<Event> events = Arrays.asList(event);
        when(eventRepository.findAll()).thenReturn(events);

        // When/Then
        when(ticketService.calculateTicketSalesCount(anyInt())).thenReturn(0L);
        
        mockMvc.perform(get("/api/events")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(event.getId().intValue())))
            .andExpect(jsonPath("$[0].title", is(event.getTitle())));

        verify(eventRepository, times(1)).findAll();
        verify(ticketService, times(1)).calculateTicketSalesCount(event.getId());
    }
}
