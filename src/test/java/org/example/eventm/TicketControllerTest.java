package org.example.eventm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eventm.api.controller.TicketController;
import org.example.eventm.api.model.Ticket;
import org.example.eventm.api.model.Event;
import org.example.eventm.api.repository.TicketRepository;
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
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
public class TicketControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketController ticketController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
        
        // Create a minimal valid Ticket with required fields
        ticket = new Ticket();
        ticket.setId(1);
        ticket.setPrice(new BigDecimal("19.99"));
        
        // Create a minimal Event (required for the Ticket)
        Event event = new Event();
        event.setId(1);
        ticket.setEvent(event);
    }

    @Test
    void testGetAllTickets() throws Exception {
        when(ticketRepository.findAll()).thenReturn(Arrays.asList(ticket));

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTicketById() throws Exception {
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));

        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateTicket() throws Exception {
        // Given
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        
        // When/Then
        String response = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticket)))
                .andDo(print()) // Print the request and response for debugging
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticket.getId()))
                .andExpect(jsonPath("$.price").value(19.99))
                .andReturn().getResponse().getContentAsString();
                
        System.out.println("Response: " + response); // Print the actual response
    }

    @Test
    void testDeleteTicket() throws Exception {
        when(ticketRepository.existsById(1)).thenReturn(true);
        Mockito.doNothing().when(ticketRepository).deleteById(1);

        mockMvc.perform(delete("/api/tickets/1"))
                .andExpect(status().isNoContent());
    }
}
