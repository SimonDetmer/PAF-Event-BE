package org.example.eventm.api.dtos;


import java.math.BigDecimal;

public class EventSummaryData {
    private Integer eventId;
    private String eventTitle;
    private Long ticketCount;
    private BigDecimal totalRevenue;

    public EventSummaryData() { }

    public EventSummaryData(Integer eventId, String eventTitle, Long ticketCount, BigDecimal totalRevenue) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.ticketCount = ticketCount;
        this.totalRevenue = totalRevenue;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Long getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(Long ticketCount) {
        this.ticketCount = ticketCount;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
