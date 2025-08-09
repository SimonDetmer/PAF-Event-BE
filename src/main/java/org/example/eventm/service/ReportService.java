package org.example.eventm.service;

import org.example.eventm.api.dtos.ReportData;
import org.example.eventm.api.dtos.ReportDataBuilder;
import org.example.eventm.api.repository.EventRepository;
import org.example.eventm.api.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ReportService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    public ReportService(TicketRepository ticketRepository, EventRepository eventRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    public ReportData generateReport() throws InterruptedException, ExecutionException {
        // Nutzen Sie den Builder, um das ReportData Objekt schrittweise zu erstellen.
        return new ReportDataBuilder()
                .withTicketSalesOverTime(ticketRepository.findTicketSalesOverTime())
                .withTicketSalesPerEvent(ticketRepository.findTicketSalesPerEvent())
                .withEventSummaries(ticketRepository.findEventSummaries())
                .withBookingHeatmap(ticketRepository.findBookingHeatmapData())
                .withLocationOccupancy(ticketRepository.findLocationOccupancyData())
                .build();
    }

    public CompletableFuture<ReportData> generateReportAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return generateReport();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Error generating report", e);
            }
        });
    }
}
