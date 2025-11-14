package org.example.eventm.api.dto;

import org.example.eventm.api.model.Event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventDto {

    private Integer id;
    private String title;
    private Integer locationId;
    private LocalDateTime eventDateTime;
    private Integer availableTickets;
    private Integer version;
    private BigDecimal ticketPrice;

    // --- static mapper ---

    public static EventDto from(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setEventDateTime(event.getEventDateTime());
        dto.setAvailableTickets(event.getAvailableTickets());
        dto.setVersion(event.getVersion());
        dto.setTicketPrice(event.getTicketPrice());
        if (event.getLocation() != null) {
            dto.setLocationId(event.getLocation().getId());
        }
        return dto;
    }

    // --- Getter & Setter ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public Integer getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(Integer availableTickets) {
        this.availableTickets = availableTickets;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }
}
