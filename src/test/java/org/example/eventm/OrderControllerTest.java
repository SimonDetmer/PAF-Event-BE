package org.example.eventm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eventm.api.controller.OrderController;
import org.example.eventm.api.dtos.CreateOrderRequest;
import org.example.eventm.api.dtos.OrderItemDto;
import org.example.eventm.api.model.Order;
import org.example.eventm.api.model.User;
import org.example.eventm.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private Order order;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        
        // Setup test user
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setOrders(new ArrayList<>());
        
        // Setup test order
        order = new Order();
        order.setId(1);
        order.setUser(user);
        order.setStatus(Order.Status.NEW);
        order.setCreatedAt(LocalDateTime.now());
        user.getOrders().add(order);
    }

    @Test
    void testGetAllOrders() throws Exception {
        // Given
        when(orderService.getAllOrders()).thenReturn(List.of(order));

        // When/Then
        mockMvc.perform(get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(order.getId())));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void testGetOrderById() throws Exception {
        // Given
        when(orderService.getOrderById(1)).thenReturn(Optional.of(order));

        // When/Then
        mockMvc.perform(get("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(order.getId())));

        verify(orderService, times(1)).getOrderById(1);
    }
    
    @Test
    void testGetOrderById_NotFound() throws Exception {
        // Given
        when(orderService.getOrderById(999)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/orders/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).getOrderById(999);
    }

    @Test
    void testCreateOrder() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        
        OrderItemDto item1 = new OrderItemDto();
        item1.setEventId(1);
        item1.setQuantity(2);
        item1.setVersion(1);
        
        request.setItems(List.of(item1));
        
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(order);

        // When/Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(order.getId())))
                .andExpect(jsonPath("$.message", is("Order created successfully")));

        verify(orderService, times(1)).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    void testDeleteOrder() throws Exception {
        // Given
        when(orderService.deleteOrder(1)).thenReturn(true);

        // When/Then
        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1);
    }

    @Test
    void testDeleteOrder_NotFound() throws Exception {
        // Given
        when(orderService.deleteOrder(999)).thenReturn(false);

        // When/Then
        mockMvc.perform(delete("/api/orders/999"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).deleteOrder(999);
    }
}
