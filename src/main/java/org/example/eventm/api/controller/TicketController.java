package org.example.eventm.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eventm.api.dto.TicketDto;
import java.util.Map;
import org.example.eventm.api.model.Ticket;
import org.example.eventm.api.service.TicketService;
import org.example.eventm.api.util.DtoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        log.info("Fetching all tickets");
        try {
            List<TicketDto> tickets = ticketService.getAllTickets().stream()
                    .map(DtoMapper::toTicketDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            log.error("Error fetching tickets", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createTicket(@Valid @RequestBody TicketDto ticketDto) {
        log.info("Creating new ticket");
        try {
            Ticket ticket = ticketService.createTicket(ticketDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toTicketDto(ticket));
        } catch (IllegalArgumentException e) {
            log.error("Invalid ticket data: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "INVALID_TICKET_DATA",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error creating ticket", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable Integer id) {
        log.info("Fetching ticket with ID: {}", id);
        try {
            return ticketService.getTicketById(id)
                    .map(ticket -> {
                        log.info("Found ticket with ID: {}", id);
                        return ResponseEntity.ok(DtoMapper.toTicketDto(ticket));
                    })
                    .orElseGet(() -> {
                        log.warn("Ticket not found with ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("Error fetching ticket with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Integer id) {
        log.info("Deleting ticket with ID: {}", id);
        try {
            if (ticketService.deleteTicket(id)) {
                log.info("Successfully deleted ticket with ID: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Ticket not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting ticket with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
