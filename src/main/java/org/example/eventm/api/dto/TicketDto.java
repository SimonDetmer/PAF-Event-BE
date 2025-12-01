package org.example.eventm.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.eventm.api.model.Ticket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketDto {

    private Integer id;
    private BigDecimal price;
    private Integer eventId;
    private Integer orderId;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static TicketDto from(Ticket ticket) {
        TicketDto dto = new TicketDto();
        dto.setId(ticket.getId());
        dto.setPrice(ticket.getPrice());
        dto.setCreatedAt(ticket.getCreatedAt());

        if (ticket.getEvent() != null) {
            dto.setEventId(ticket.getEvent().getId());
        }
        if (ticket.getOrder() != null) {
            dto.setOrderId(ticket.getOrder().getId());
        }
        if (ticket.getStatus() != null) {
            dto.setStatus(ticket.getStatus().name());
        }

        return dto;
    }
}
