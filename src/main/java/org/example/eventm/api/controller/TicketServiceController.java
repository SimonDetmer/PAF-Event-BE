package org.example.eventm.api.controller;


import org.example.eventm.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket-service")
public class TicketServiceController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/{id}")
    public long getCalculation(@PathVariable Integer id) {
        return ticketService.calculateTicketSalesCount(id);
    }
}