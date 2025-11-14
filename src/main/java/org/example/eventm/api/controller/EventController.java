package org.example.eventm.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eventm.api.dto.CreateEventRequest;
import org.example.eventm.api.dto.EventDto;
import org.example.eventm.api.model.Event;
import org.example.eventm.api.repository.EventRepository;
import org.example.eventm.api.util.DtoMapper;
import org.example.eventm.exception.ResourceNotFoundException;
import org.example.eventm.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventRepository eventRepository;

    @GetMapping
    public ResponseEntity<List<EventDto>> getAll() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(DtoMapper.toEventDtos(events));
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody CreateEventRequest request) {
        try {
            Event event = eventService.createEvent(request);
            EventDto dto = DtoMapper.toEventDto(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                            "error", "LOCATION_NOT_FOUND",
                            "message", e.getMessage()
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "error", "INTERNAL_ERROR",
                            "message", e.getMessage()
                    )
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Integer id) {
        try {
            Event event = eventService.getEventById(id);
            return ResponseEntity.ok(DtoMapper.toEventDto(event));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "error", "EVENT_NOT_FOUND",
                            "message", e.getMessage()
                    )
            );
        }
    }
}
