package org.example.eventm.api.dto;

import java.util.List;

public class ReportData {
    private List<TimeSeriesData> ticketSalesOverTime;

    private List<EventSummaryData> ticketSalesPerEvent;

    private List<EventSummaryData> eventSummaries;

    private List<HeatmapData> bookingHeatmap;

    private List<LocationOccupancyData> locationOccupancy;

    public List<TimeSeriesData> getTicketSalesOverTime() {
        return ticketSalesOverTime;
    }

    public void setTicketSalesOverTime(List<TimeSeriesData> ticketSalesOverTime) {
        this.ticketSalesOverTime = ticketSalesOverTime;
    }

    public List<EventSummaryData> getTicketSalesPerEvent() {
        return ticketSalesPerEvent;
    }

    public void setTicketSalesPerEvent(List<EventSummaryData> ticketSalesPerEvent) {
        this.ticketSalesPerEvent = ticketSalesPerEvent;
    }

    public List<EventSummaryData> getEventSummaries() {
        return eventSummaries;
    }

    public void setEventSummaries(List<EventSummaryData> eventSummaries) {
        this.eventSummaries = eventSummaries;
    }

    public List<HeatmapData> getBookingHeatmap() {
        return bookingHeatmap;
    }

    public void setBookingHeatmap(List<HeatmapData> bookingHeatmap) {
        this.bookingHeatmap = bookingHeatmap;
    }

    public List<LocationOccupancyData> getLocationOccupancy() {
        return locationOccupancy;
    }

    public void setLocationOccupancy(List<LocationOccupancyData> locationOccupancy) {
        this.locationOccupancy = locationOccupancy;
    }
}
