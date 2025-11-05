package org.example.eventm.api.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @Valid
    @NotNull(message = "Order items are required")
    private List<OrderItemDto> items;
}
