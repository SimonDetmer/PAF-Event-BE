package org.example.eventm.api.dto;

import java.util.ArrayList;
import java.util.List;

public class ReportDataBuilder {

    private final ReportData reportData;

    public ReportDataBuilder() {
        this.reportData = new ReportData();
    }

    // ----------------------------------------------------
    // Zeitlicher Verlauf der Ticket-Verkäufe
    // ----------------------------------------------------
    public ReportDataBuilder withTicketSalesOverTime(List<TimeSeriesData> ticketSalesOverTime) {
        if (ticketSalesOverTime == null) {
            ticketSalesOverTime = new ArrayList<>();
        }
        reportData.setTicketSalesOverTime(ticketSalesOverTime);
        return this;
    }

    // ----------------------------------------------------
    // Ticket-Sales pro Event (z. B. für Pie-Chart)
    // ----------------------------------------------------
    public ReportDataBuilder withTicketSalesPerEvent(List<EventSummaryData> ticketSalesPerEvent) {
        if (ticketSalesPerEvent == null) {
            ticketSalesPerEvent = new ArrayList<>();
        }
        reportData.setTicketSalesPerEvent(ticketSalesPerEvent);
        return this;
    }

    // ----------------------------------------------------
    // Event-Summaries (Tabelle)
    // ----------------------------------------------------
    public ReportDataBuilder withEventSummaries(List<EventSummaryData> eventSummaries) {
        if (eventSummaries == null) {
            eventSummaries = new ArrayList<>();
        }
        reportData.setEventSummaries(eventSummaries);
        return this;
    }

    // ----------------------------------------------------
    // Heatmap-Daten (Booking-Heatmap)
    // ----------------------------------------------------
    public ReportDataBuilder withBookingHeatmap(List<HeatmapData> bookingHeatmap) {
        if (bookingHeatmap == null) {
            bookingHeatmap = new ArrayList<>();
        }
        reportData.setBookingHeatmap(bookingHeatmap);
        return this;
    }

    // ----------------------------------------------------
    // Location-Auslastung
    // ----------------------------------------------------
    public ReportDataBuilder withLocationOccupancy(List<LocationOccupancyData> locationOccupancy) {
        if (locationOccupancy == null) {
            locationOccupancy = new ArrayList<>();
        }
        reportData.setLocationOccupancy(locationOccupancy);
        return this;
    }

    // ----------------------------------------------------
    // Fertiges Objekt
    // ----------------------------------------------------
    public ReportData build() {
        return reportData;
    }
}
