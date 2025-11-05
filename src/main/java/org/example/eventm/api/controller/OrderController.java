package org.example.eventm.api.controller;

import jakarta.validation.Valid;
import org.example.eventm.api.dtos.CreateOrderRequest;
import org.example.eventm.api.model.Order;
import org.example.eventm.service.OrderService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            Order order = orderService.createOrder(request);
            return ResponseEntity.ok(order);
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                    "error", "CONCURRENT_MODIFICATION",
                    "message", "The event was modified by another transaction. Please refresh and try again."
                ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Integer id, @Valid @RequestBody Order updatedOrder) {
        return orderRepository.findById(id)
                .map(existingOrder -> {
                    existingOrder.setStatus(updatedOrder.getStatus());

                    // Optional: Wenn Tickets aktualisiert werden sollen, setze sie neu.
                    // Hier wird die alte Liste ersetzt:
                    if (updatedOrder.getTickets() != null) {
                        // Setze zuerst die Order bei jedem Ticket der neuen Liste.
                        updatedOrder.getTickets().forEach(ticket -> ticket.setOrder(existingOrder));
                        existingOrder.setTickets(updatedOrder.getTickets());
                    }

                    Order savedOrder = orderRepository.save(existingOrder);
                    return ResponseEntity.ok(savedOrder);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
