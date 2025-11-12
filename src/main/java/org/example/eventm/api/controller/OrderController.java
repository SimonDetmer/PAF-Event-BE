package org.example.eventm.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eventm.api.dtos.CreateOrderRequest;
import org.example.eventm.api.dtos.OrderDto;
import org.example.eventm.api.model.Order;
import org.example.eventm.service.OrderService;
import org.example.eventm.api.util.DtoMapper;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        log.info("Fetching all orders");
        try {
            List<OrderDto> orderDtos = DtoMapper.toOrderDtos(orderService.getAllOrders());
            log.info("Found {} orders", orderDtos.size());
            return ResponseEntity.ok(orderDtos);
        } catch (Exception e) {
            log.error("Error fetching orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error fetching orders", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());
        try {
            Order order = orderService.createOrder(request);
            OrderDto orderDto = DtoMapper.toOrderDto(order);
            
            log.info("Order created successfully with ID: {}", order.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Order created successfully",
                "order", orderDto
            ));
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid order data: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("INVALID_ORDER_DATA", e.getMessage()));
                
        } catch (OptimisticLockingFailureException e) {
            log.warn("Concurrent modification while creating order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse(
                    "CONCURRENT_MODIFICATION",
                    "The event was modified by another transaction. Please refresh and try again."
                ));
        } catch (Exception e) {
            log.error("Error creating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error creating order", e));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Integer id) {
        log.info("Fetching order with ID: {}", id);
        try {
            return orderService.getOrderById(id)
                    .map(order -> {
                        log.info("Found order with ID: {}", id);
                        return ResponseEntity.ok(DtoMapper.toOrderDto(order));
                    })
                    .orElseGet(() -> {
                        log.warn("Order not found with ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("Error fetching order with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error fetching order", e));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Integer id, @Valid @RequestBody Order updatedOrder) {
        log.info("Updating order with ID: {}", id);
        try {
            return orderService.updateOrder(id, updatedOrder)
                    .map(order -> {
                        log.info("Updated order with ID: {}", id);
                        return ResponseEntity.ok(convertToOrderDto(order));
                    })
                    .orElseGet(() -> {
                        log.warn("Order not found for update, ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("Error updating order with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error updating order", e));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer id) {
        log.info("Deleting order with ID: {}", id);
        try {
            if (orderService.deleteOrder(id)) {
                log.info("Successfully deleted order with ID: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Order not found for deletion, ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting order with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error deleting order", e));
        }
    }

    // Helper methods
    private Map<String, Object> convertToOrderDto(Order order) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", order.getId());
        dto.put("status", order.getStatus().name());
        dto.put("createdAt", order.getCreatedAt() != null ? 
            order.getCreatedAt().format(formatter) : null);
        
        // Add user info
        if (order.getUser() != null) {
            Map<String, Object> userDto = new HashMap<>();
            userDto.put("id", order.getUser().getId());
            userDto.put("email", order.getUser().getEmail());
            dto.put("user", userDto);
        }
        
        // Add tickets info if needed
        if (order.getTickets() != null && !order.getTickets().isEmpty()) {
            dto.put("ticketCount", order.getTickets().size());
        }
        
        return dto;
    }
    
    private Map<String, Object> createErrorResponse(String error, String message) {
        return Map.of(
            "error", error,
            "message", message
        );
    }
    
    private Map<String, Object> createErrorResponse(String error, Exception e) {
        return createErrorResponse(error, e.getMessage());
    }
    
    /**
     * Safely converts a status string to Order.Status enum.
     * Defaults to PENDING if the status is invalid.
     */
    private Order.Status convertToOrderStatus(String status) {
        try {
            return Order.Status.fromString(status);
        } catch (Exception e) {
            log.warn("Invalid status '{}' provided, defaulting to PENDING", status);
            return Order.Status.PENDING;
        }
    }
}
