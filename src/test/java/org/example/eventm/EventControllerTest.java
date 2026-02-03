package org.example.eventm;

import org.example.eventm.api.controller.EventController;
import org.example.eventm.api.model.Event;
import org.example.eventm.api.model.Location;
import org.example.eventm.api.repository.EventRepository;
import org.example.eventm.service.TicketReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.math.BigInteger;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TicketReportService ticketReportService;

    @InjectMocks
    private EventController eventController;

    private Event event;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();

        // Minimal valid Location (NEW model: name, city, capacity)
        Location location = new Location();
        location.setId(1);
        location.setName("Test Location");
        location.setCity("Konstanz");
        location.setCapacity(BigInteger.valueOf(100));


        event = new Event();
        event.setId(1);
        event.setTitle("Test Event");
        event.setLocation(location);
        event.setEventDateTime(LocalDateTime.now().plusDays(1));
    }

    @Test
    void testGetAllEvents() throws Exception {
        // Given
        List<Event> events = Collections.singletonList(event);
        when(eventRepository.findAll()).thenReturn(events);

        // If your controller enriches events with ticket sales count, mock it:
        when(ticketReportService.calculateTicketSalesCount(anyInt())).thenReturn(0L);

        // When/Then
        mockMvc.perform(get("/api/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(event.getId())))
                .andExpect(jsonPath("$[0].title", is(event.getTitle())));

        verify(eventRepository, times(1)).findAll();
        verify(ticketReportService, times(1)).calculateTicketSalesCount(event.getId());
    }
}
