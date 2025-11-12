package org.example.eventm.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.eventm.api.model.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderDto {
    private Integer id;
    private String status;
    private Long userId;
    private List<TicketDto> tickets;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    public static OrderDto from(org.example.eventm.api.model.Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus().name());
        dto.setUserId(order.getUser().getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setTickets(order.getTickets().stream()
                .map(TicketDto::from)
                .collect(Collectors.toList()));
        return dto;
    }
}
