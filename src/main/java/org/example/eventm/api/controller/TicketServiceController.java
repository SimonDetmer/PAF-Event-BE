package org.example.eventm.api.controller;


import org.example.eventm.service.TicketReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket-service")
public class TicketServiceController {

    @Autowired
    private TicketReportService ticketReportService;

    @GetMapping("/{id}")
    public long getCalculation(@PathVariable Integer id) {
        return ticketReportService.calculateTicketSalesCount(id);
    }
}