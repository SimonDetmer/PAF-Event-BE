package org.example.eventm.api.dtos;

import java.util.ArrayList;
import java.util.List;

public class ReportDataBuilder {
    private final ReportData reportData;

    public ReportDataBuilder() {
        this.reportData = new ReportData();
    }

    public ReportDataBuilder withTicketSalesOverTime(List<TimeSeriesData> ticketSalesOverTime) {
        reportData.setTicketSalesOverTime(ticketSalesOverTime);
        return this;
    }

    public ReportDataBuilder withTicketSalesPerEvent(List<EventSummaryData> ticketSalesPerEvent) {
        reportData.setTicketSalesPerEvent(ticketSalesPerEvent);
        return this;
    }

    public ReportDataBuilder withEventSummaries(List<EventSummaryData> eventSummaries) {
        reportData.setEventSummaries(eventSummaries);
        return this;
    }

    // Wenn findBookingHeatmapData() ein einzelnes HeatmapData liefert, packen wir es in eine Liste.
    public ReportDataBuilder withBookingHeatmap(HeatmapData bookingHeatmap) {
        List<HeatmapData> heatmapList = new ArrayList<>();
        if (bookingHeatmap != null) {
            heatmapList.add(bookingHeatmap);
        }
        reportData.setBookingHeatmap(heatmapList);
        return this;
    }

    // Analog für LocationOccupancyData:
    public ReportDataBuilder withLocationOccupancy(LocationOccupancyData locationOccupancy) {
        List<LocationOccupancyData> occupancyList = new ArrayList<>();
        if (locationOccupancy != null) {
            occupancyList.add(locationOccupancy);
        }
        reportData.setLocationOccupancy(occupancyList);
        return this;
    }

    public ReportData build() {
        return reportData;
    }
}
