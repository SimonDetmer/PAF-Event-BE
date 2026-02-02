package org.example.eventm.api.service;

import org.example.eventm.api.model.Ticket;
import org.example.eventm.api.service.TicketService;
import org.example.eventm.api.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.example.eventm.api.dto.TicketDto;
import org.example.eventm.api.service.TicketService;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public long calculateTicketSalesCount(Integer eventId) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getEvent() != null && ticket.getEvent().getId().equals(eventId))
                .filter(ticket -> ticket.getOrder() != null)
                .count();
    }

    // --- Interface-Methoden ---
    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Optional<Ticket> getTicketById(Integer id) {
        return ticketRepository.findById(id);
    }

    @Override
    public Ticket createTicket(TicketDto ticketDto) {
        Ticket t = new Ticket();
        return ticketRepository.save(t);
    }

    @Override
    public boolean deleteTicket(Integer id) {
        if (!ticketRepository.existsById(id)) return false;
        ticketRepository.deleteById(id);
        return true;
    }
}
