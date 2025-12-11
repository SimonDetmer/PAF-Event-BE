package org.example.eventm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eventm.api.controller.OrderController;
import org.example.eventm.api.dto.CreateOrderRequest;
import org.example.eventm.api.dto.OrderItemDto;
import org.example.eventm.api.model.Order;
import org.example.eventm.api.model.User;
import org.example.eventm.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
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
        user.setId(1L); // <-- Long, nicht int
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

    private CreateOrderRequest buildCreateOrderRequest() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L); // <-- Long

        OrderItemDto item = new OrderItemDto();
        item.setEventId(1);
        item.setQuantity(2);
        item.setVersion(0);

        request.setItems(List.of(item));
        return request;
    }

    // -------------------------------------------------------------------------
    // GET /api/orders
    // -------------------------------------------------------------------------
    @Test
    void testGetAllOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(order.getId())));

        verify(orderService, times(1)).getAllOrders();
    }

    // -------------------------------------------------------------------------
    // GET /api/orders/{id}
    // -------------------------------------------------------------------------
    @Test
    void testGetOrderById() throws Exception {
        when(orderService.getOrderById(1)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(order.getId())));

        verify(orderService, times(1)).getOrderById(1);
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        when(orderService.getOrderById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).getOrderById(999);
    }

    // -------------------------------------------------------------------------
    // POST /api/orders – Happy Path
    // -------------------------------------------------------------------------
    @Test
    void testCreateOrder_Success() throws Exception {
        CreateOrderRequest request = buildCreateOrderRequest();

        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("Order created successfully")))
                // Die ID steckt unter "order.id", nicht direkt auf Root-Level
                .andExpect(jsonPath("$.order.id", is(order.getId())));

        verify(orderService, times(1)).createOrder(any(CreateOrderRequest.class));
    }

    // -------------------------------------------------------------------------
    // POST /api/orders – Invalid Data (400)
    // -------------------------------------------------------------------------
    @Test
    void testCreateOrder_InvalidData_BadRequest() throws Exception {
        CreateOrderRequest request = buildCreateOrderRequest();

        when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("INVALID_ORDER_DATA")))
                .andExpect(jsonPath("$.message", containsString("Invalid data")));

        verify(orderService, times(1)).createOrder(any(CreateOrderRequest.class));
    }

    // -------------------------------------------------------------------------
    // POST /api/orders – Concurrency (409)
    // -------------------------------------------------------------------------
    @Test
    void testCreateOrder_ConcurrentModification_Conflict() throws Exception {
        CreateOrderRequest request = buildCreateOrderRequest();

        when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenThrow(new OptimisticLockingFailureException("Concurrent update"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("CONCURRENT_MODIFICATION")))
                .andExpect(jsonPath("$.message",
                        is("The event was modified by another transaction. Please refresh and try again.")));

        verify(orderService, times(1)).createOrder(any(CreateOrderRequest.class));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/orders/{id}
    // -------------------------------------------------------------------------
    @Test
    void testDeleteOrder() throws Exception {
        when(orderService.deleteOrder(1)).thenReturn(true);

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1);
    }

    @Test
    void testDeleteOrder_NotFound() throws Exception {
        when(orderService.deleteOrder(999)).thenReturn(false);

        mockMvc.perform(delete("/api/orders/999"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).deleteOrder(999);
    }
}
