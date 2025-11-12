package org.example.eventm.api.service;

import org.example.eventm.api.dto.TicketDto;
import org.example.eventm.api.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    List<Ticket> getAllTickets();
    Optional<Ticket> getTicketById(Integer id);
    Ticket createTicket(TicketDto ticketDto);
    boolean deleteTicket(Integer id);
}
