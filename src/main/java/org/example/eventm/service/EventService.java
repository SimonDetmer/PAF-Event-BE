package org.example.eventm.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.eventm.api.dto.CreateEventRequest;
import org.example.eventm.api.model.Event;
import org.example.eventm.api.model.Location;
import org.example.eventm.api.model.Ticket;
import org.example.eventm.api.repository.EventRepository;
import org.example.eventm.api.repository.LocationRepository;
import org.example.eventm.api.repository.TicketRepository;
import org.example.eventm.api.service.TicketStateService;
import org.example.eventm.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final LocationRepository locationRepository;
    private final TicketStateService ticketStateService;

    @Transactional
    public Event createEvent(CreateEventRequest dto) {

        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found: " + dto.getLocationId()));

        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setEventDateTime(dto.getEventDateTime());
        event.setLocation(location);
        event.setAvailableTickets(dto.getInitialTickets());
        event.setTicketPrice(dto.getTicketPrice());

        Event savedEvent = eventRepository.save(event);

        // Tickets erzeugen (✅ inkl. status = AVAILABLE)
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < dto.getInitialTickets(); i++) {
            Ticket t = new Ticket();
            t.setEvent(savedEvent);
            t.setPrice(dto.getTicketPrice());

            // ✅ WICHTIG: status setzen, sonst NOT NULL Fehler in DB
            ticketStateService.initializeNewTicket(t);

            tickets.add(t);
        }

        ticketRepository.saveAll(tickets);
        savedEvent.setTickets(tickets);

        return savedEvent;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Integer id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
    }

    // Sicheres Löschen inkl. Ticket-Cleanup
    @Transactional
    public void deleteEvent(Integer id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found: " + id);
        }

        ticketRepository.deleteAllByEventId(id);
        eventRepository.deleteById(id);
    }
}
