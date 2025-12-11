package org.example.eventm.service;

import org.example.eventm.api.dto.CreateOrderRequest;
import org.example.eventm.api.dto.OrderItemDto;
import org.example.eventm.api.model.Event;
import org.example.eventm.api.model.Order;
import org.example.eventm.api.model.Ticket;
import org.example.eventm.api.model.User;
import org.example.eventm.api.repository.EventRepository;
import org.example.eventm.api.repository.OrderRepository;
import org.example.eventm.api.repository.UserRepository;
import org.example.eventm.exception.InsufficientTicketsException;
import org.example.eventm.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Event event;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setEmail("customer@test.de");

        event = new Event();
        event.setId(10);
        event.setTitle("Test Event");
        event.setAvailableTickets(100);
        event.setTicketPrice(BigDecimal.valueOf(25.00));
    }

    private CreateOrderRequest buildRequest(int quantity) {
        OrderItemDto item = new OrderItemDto();
        item.setEventId(event.getId());
        item.setQuantity(quantity);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(user.getId());
        request.setItems(List.of(item));

        return request;
    }

    @Test
    void createOrder_successfullyCreatesOrderAndReducesTickets() {
        // Arrange
        CreateOrderRequest request = buildRequest(3);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        // save(order) soll einfach das gegebene Objekt zurückgeben
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order created = orderService.createOrder(request);

        // Assert
        assertNotNull(created);
        assertEquals(Order.Status.COMPLETED, created.getStatus());
        assertEquals(3, created.getTickets().size());
        assertEquals(97, event.getAvailableTickets()); // 100 - 3

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(eventRepository, atLeastOnce()).save(event);
    }

    @Test
    void createOrder_throwsInsufficientTicketsException_whenNotEnoughTickets() {
        // Arrange
        event.setAvailableTickets(1); // zu wenig
        CreateOrderRequest request = buildRequest(5);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        // Act + Assert
        InsufficientTicketsException ex = assertThrows(
                InsufficientTicketsException.class,
                () -> orderService.createOrder(request)
        );

        assertTrue(ex.getMessage().contains("Not enough tickets available"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_throwsResourceNotFound_whenUserMissing() {
        // Arrange
        CreateOrderRequest request = buildRequest(1);

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(request));
        verify(eventRepository, never()).findById(anyInt());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_propagatesOptimisticLockingException() {
        // Arrange
        CreateOrderRequest request = buildRequest(1);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        // beim Speichern der Order wird ein Optimistic-Locking-Fehler ausgelöst
        when(orderRepository.save(any(Order.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(Event.class, event.getId()));

        // Act + Assert
        assertThrows(ObjectOptimisticLockingFailureException.class,
                () -> orderService.createOrder(request));
    }
}
