package org.example.eventm.api.service;

import org.example.eventm.api.model.Ticket;
import org.example.eventm.api.model.TicketStatus;
import org.springframework.stereotype.Service;

@Service
public class TicketStateService {

    private TicketStatus getEffectiveStatus(Ticket ticket) {
        // Falls noch kein Status gesetzt wurde, behandeln wir das Ticket als AVAILABLE
        return ticket.getStatus() != null ? ticket.getStatus() : TicketStatus.AVAILABLE;
    }

    public void initializeNewTicket(Ticket ticket) {
        ticket.setStatus(TicketStatus.AVAILABLE);
    }

    public void reserve(Ticket ticket) {
        TicketStatus current = getEffectiveStatus(ticket);
        ticket.setStatus(current.reserve());
    }

    public void purchase(Ticket ticket) {
        TicketStatus current = getEffectiveStatus(ticket);
        ticket.setStatus(current.purchase());
    }

    public void cancel(Ticket ticket) {
        TicketStatus current = getEffectiveStatus(ticket);
        ticket.setStatus(current.cancel());
    }

    public void expire(Ticket ticket) {
        TicketStatus current = getEffectiveStatus(ticket);
        ticket.setStatus(current.expire());
    }
}
