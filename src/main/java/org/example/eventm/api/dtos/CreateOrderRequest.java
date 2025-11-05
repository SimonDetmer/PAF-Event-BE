package org.example.eventm.api.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateOrderRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @Valid
    @NotNull(message = "Order items are required")
    private List<OrderItemDto> items;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }
}
