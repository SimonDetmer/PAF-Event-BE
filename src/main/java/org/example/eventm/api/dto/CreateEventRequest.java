package org.example.eventm.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateEventRequest {

    @NotBlank
    private String title;

    @NotNull
    private Integer locationId;

    @NotNull
    private LocalDateTime eventDateTime;

    @NotNull
    @Positive
    private Integer initialTickets;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal ticketPrice;

    // --- Getter & Setter ---

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

    public Integer getInitialTickets() {
        return initialTickets;
    }

    public void setInitialTickets(Integer initialTickets) {
        this.initialTickets = initialTickets;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }
}
