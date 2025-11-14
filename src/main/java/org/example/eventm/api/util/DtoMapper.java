package org.example.eventm.api.util;

import org.example.eventm.api.dto.EventDto;
import org.example.eventm.api.dto.OrderDto;
import org.example.eventm.api.dto.TicketDto;
import org.example.eventm.api.model.Event;
import org.example.eventm.api.model.Order;
import org.example.eventm.api.model.Ticket;

import java.util.List;
import java.util.stream.Collectors;

public class DtoMapper {

    public static OrderDto toOrderDto(Order order) {
        if (order == null) {
            return null;
        }
        return OrderDto.from(order);
    }

    public static List<OrderDto> toOrderDtos(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        return orders.stream()
                .map(OrderDto::from)
                .collect(Collectors.toList());
    }

    public static TicketDto toTicketDto(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        return TicketDto.from(ticket);
    }

    public static List<TicketDto> toTicketDtos(List<Ticket> tickets) {
        if (tickets == null) {
            return null;
        }
        return tickets.stream()
                .map(TicketDto::from)
                .collect(Collectors.toList());
    }

    // --- NEU: Event Mapping ---

    public static EventDto toEventDto(Event event) {
        if (event == null) {
            return null;
        }
        return EventDto.from(event);
    }

    public static List<EventDto> toEventDtos(List<Event> events) {
        if (events == null) {
            return null;
        }
        return events.stream()
                .map(EventDto::from)
                .collect(Collectors.toList());
    }
}
