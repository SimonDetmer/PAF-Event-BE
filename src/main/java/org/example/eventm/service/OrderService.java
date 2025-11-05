package org.example.eventm.service;

import jakarta.transaction.Transactional;
import org.example.eventm.api.dtos.CreateOrderRequest;
import org.example.eventm.api.dtos.OrderItemDto;
import org.example.eventm.api.model.*;
import org.example.eventm.api.repository.EventRepository;
import org.example.eventm.api.repository.OrderRepository;
import org.example.eventm.api.repository.UserRepository;
import org.example.eventm.exception.InsufficientTicketsException;
import org.example.eventm.exception.ResourceNotFoundException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                       EventRepository eventRepository,
                       UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // Find user
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        // Create new order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        // Process each order item
        for (OrderItemDto item : request.getItems()) {
            // Find the event with the given ID
            Event event = eventRepository.findById(item.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + item.getEventId()));

            // Check version for optimistic locking
            if (!event.getVersion().equals(item.getVersion())) {
                throw new ObjectOptimisticLockingFailureException(Event.class, event.getId());
            }

            // Check ticket availability
            if (event.getAvailableTickets() < item.getQuantity()) {
                throw new InsufficientTicketsException(
                    "Not enough tickets available for event: " + event.getTitle() + 
                    ". Available: " + event.getAvailableTickets());
            }

            // Update available tickets
            event.setAvailableTickets(event.getAvailableTickets() - item.getQuantity());
            eventRepository.save(event);

            // Create tickets for the order
            for (int i = 0; i < item.getQuantity(); i++) {
                Ticket ticket = new Ticket();
                ticket.setEvent(event);
                ticket.setOrder(order);
                ticket.setPrice(event.getTickets().get(0).getPrice()); // Assuming single price per event
                order.getTickets().add(ticket);
            }
        }

        // Mark order as completed
        order.setStatus(Order.Status.COMPLETED);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}
