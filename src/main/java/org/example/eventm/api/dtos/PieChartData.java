package org.example.eventm.api.dtos;


public class PieChartData {
    private Integer eventId;
    private String eventTitle;
    private Long ticketCount;

    public PieChartData() { }

    public PieChartData(Integer eventId, String eventTitle, Long ticketCount) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.ticketCount = ticketCount;
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
}
