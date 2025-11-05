package org.example.eventm.api.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderItemDto {
    @NotNull(message = "Event ID is required")
    private Integer eventId;
    
    @Positive(message = "Quantity must be positive")
    private int quantity;
    
    @NotNull(message = "Version is required")
    private Integer version;

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
