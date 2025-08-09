package org.example.eventm.api.controller;

import jakarta.validation.Valid;
import org.example.eventm.api.model.Event;
import org.example.eventm.api.repository.EventRepository;
import org.example.eventm.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/events")
public class EventController {

    private final EventRepository eventRepository;
    private final TicketService ticketService;


    public EventController(EventRepository eventRepository, TicketService ticketService) {
        this.eventRepository = eventRepository;
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllEvents() {
        List<Event> events = eventRepository.findAll();

        List<Map<String, Object>> responseList = events.stream().map(event -> {
            // Ticketverkaufszahlen für das jeweilige Event ermitteln
            long ticketSaleCount = ticketService.calculateTicketSalesCount(event.getId());

            Map<String, Object> response = new HashMap<>();

            // Basisdaten des Events
            response.put("id", event.getId());
            response.put("title", event.getTitle());
            response.put("location", event.getLocation());
            response.put("eventDateTime", event.getEventDateTime());
            response.put("tickets", event.getTickets());

            // Zusätzliche Informationen
            response.put("ticketSaleCount", ticketSaleCount);

            // Berechnung der verfügbaren Tickets aus der Location-Kapazität (als BigInteger)
            if (event.getLocation() != null && event.getLocation().getCapacity() != null) {
                BigInteger availableTickets = event.getLocation().getCapacity()
                        .subtract(BigInteger.valueOf(ticketSaleCount));
                response.put("availableTickets", availableTickets);
            } else {
                response.put("availableTickets", null);
            }
            return response;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    public Event createEvent(@Valid @RequestBody Event event) {
        return eventRepository.save(event);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Integer id) {
        long ticketSaleCount = ticketService.calculateTicketSalesCount(id);

        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Integer id, @Valid @RequestBody Event updatedEvent) {
        return eventRepository.findById(id)
                .map(existingEvent -> {
                    existingEvent.setTitle(updatedEvent.getTitle());
                    existingEvent.setEventDateTime(updatedEvent.getEventDateTime());
                    existingEvent.setLocation(updatedEvent.getLocation());
                    // Optional: Je nach Bedarf auch Tickets aktualisieren.
                    Event savedEvent = eventRepository.save(existingEvent);
                    return ResponseEntity.ok(savedEvent);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
