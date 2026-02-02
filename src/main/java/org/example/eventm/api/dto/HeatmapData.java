package org.example.eventm.api.dto;



public class HeatmapData {
    private Integer timeSlot;  // z.B. 8 für "08:00-09:00"
    private Long ticketCount;

    public HeatmapData() { }

    // Existierender Konstruktor
    public HeatmapData(Integer timeSlot, Long ticketCount) {
        this.timeSlot = timeSlot;
        this.ticketCount = ticketCount;
    }

    public Integer getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(Integer timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Long getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(Long ticketCount) {
        this.ticketCount = ticketCount;
    }
}
