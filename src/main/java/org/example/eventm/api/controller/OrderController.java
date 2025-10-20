package org.example.eventm.api.controller;

import jakarta.validation.Valid;
import org.example.eventm.api.model.Order;
import org.example.eventm.api.repository.OrderRepository;
import org.example.eventm.api.repository.TicketRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;


    private final TicketRepository ticketRepository;

    public OrderController(OrderRepository orderRepository, TicketRepository ticketRepository) {
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @PostMapping
    public Order createOrder(@Valid @RequestBody Order order) {
        // Bei jedem Ticket sicherstellen, dass die ID null ist
        if (order.getTickets() != null) {
            order.getTickets().forEach(ticket -> {
                ticket.setId(null);
                ticket.setOrder(order); // Order-Beziehung setzen
            });

        }

        return orderRepository.save(order);
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
