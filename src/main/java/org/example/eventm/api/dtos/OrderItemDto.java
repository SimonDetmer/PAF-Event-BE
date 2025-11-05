package org.example.eventm.api.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderItemDto {
    @NotNull(message = "Event ID is required")
    private Long eventId;
    
    @Positive(message = "Quantity must be positive")
    private int quantity;
    
    @NotNull(message = "Version is required")
    private Integer version;
}
