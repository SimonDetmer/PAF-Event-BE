package org.example.eventm.service;

import jakarta.transaction.Transactional;
import org.example.eventm.api.dto.CreateOrderRequest;
import org.example.eventm.api.dto.OrderItemDto;
import org.example.eventm.api.model.Event;
import org.example.eventm.api.model.Order;
import org.example.eventm.api.model.Ticket;
import org.example.eventm.api.model.User;
import org.example.eventm.api.repository.EventRepository;
import org.example.eventm.api.repository.OrderRepository;
import org.example.eventm.api.repository.UserRepository;
import org.example.eventm.api.service.TicketStateService;
import org.example.eventm.exception.InsufficientTicketsException;
import org.example.eventm.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketStateService ticketStateService;

    public OrderService(OrderRepository orderRepository,
                        EventRepository eventRepository,
                        UserRepository userRepository,
                        TicketStateService ticketStateService) {
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.ticketStateService = ticketStateService;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {

        // User laden
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        // Für jedes Event im Warenkorb
        for (OrderItemDto item : request.getItems()) {

            Event event = eventRepository.findById(item.getEventId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Event not found with id: " + item.getEventId()));

            // Verfügbarkeit prüfen
            if (event.getAvailableTickets() < item.getQuantity()) {
                throw new InsufficientTicketsException(
                        "Not enough tickets available for event: " + event.getTitle() +
                                ". Available: " + event.getAvailableTickets());
            }

            // Bestand reduzieren
            event.setAvailableTickets(event.getAvailableTickets() - item.getQuantity());
            eventRepository.save(event);

            // Tickets erzeugen
            for (int i = 0; i < item.getQuantity(); i++) {
                Ticket ticket = new Ticket();
                ticket.setEvent(event);
                ticket.setOrder(order);
                ticket.setPrice(event.getTicketPrice());

                // State Pattern korrekt:
                ticketStateService.initializeNewTicket(ticket); // AVAILABLE
                ticketStateService.purchase(ticket);            // → PURCHASED

                order.getTickets().add(ticket);
            }
        }

        order.setStatus(Order.Status.COMPLETED);
        return orderRepository.save(order);
    }

    // -------------------------
    // Standard CRUD
    // -------------------------

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public java.util.Optional<Order> getOrderById(Integer id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public java.util.Optional<Order> updateOrder(Integer id, Order updatedOrder) {
        return orderRepository.findById(id)
                .map(existingOrder -> {
                    existingOrder.setStatus(updatedOrder.getStatus());

                    if (updatedOrder.getTickets() != null) {
                        existingOrder.getTickets().clear();
                        updatedOrder.getTickets().forEach(ticket -> {
                            ticket.setOrder(existingOrder);
                            existingOrder.getTickets().add(ticket);
                        });
                    }

                    return orderRepository.save(existingOrder);
                });
    }

    @Transactional
    public boolean deleteOrder(Integer id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
