package org.example.eventm.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.eventm.api.dto.TicketDto;
import org.example.eventm.api.model.Ticket;
import org.example.eventm.api.repository.TicketRepository;
import org.example.eventm.api.service.TicketService;
import org.example.eventm.api.service.TicketStateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStateService ticketStateService;

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Optional<Ticket> getTicketById(Integer id) {
        return ticketRepository.findById(id);
    }

    @Override
    @Transactional
    public Ticket createTicket(TicketDto ticketDto) {
        // Map DTO to entity using the mapper
        Ticket ticket = new Ticket();

        // TODO: Event und Order ggf. per Repository laden, wenn du das Feature brauchst
        if (ticketDto.getEventId() != null) {
            // eventRepository.findById(ticketDto.getEventId()).ifPresent(ticket::setEvent);
        }
        if (ticketDto.getOrderId() != null) {
            // orderRepository.findById(ticketDto.getOrderId()).ifPresent(ticket::setOrder);
        }

        ticket.setPrice(ticketDto.getPrice());

        // State-Pattern: neu angelegtes Ticket -> AVAILABLE
        ticketStateService.initializeNewTicket(ticket);

        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public boolean deleteTicket(Integer id) {
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
