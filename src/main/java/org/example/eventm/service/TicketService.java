package org.example.eventm.service;

import org.example.eventm.api.model.Ticket;
import org.example.eventm.api.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public long calculateTicketSalesCount(Integer eventId) {
        List<Ticket> entities = ticketRepository.findAll();

        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getEvent() != null && ticket.getEvent().getId().equals(eventId))
                .filter(ticket -> ticket.getOrder() != null)
                .count();
    }
}
