package org.example.eventm.api.dtos;



public class HeatmapData {
    private Integer timeSlot;  // z.B. 8 für "08:00-09:00"
    private Long ticketCount;

    public HeatmapData() { }

    // Existierender Konstruktor
    public HeatmapData(Integer timeSlot, Long ticketCount) {
        this.timeSlot = timeSlot;
        this.ticketCount = ticketCount;
    }

    // Neuer Konstruktor für String als Zeit-Slot (z.B. "08")
    public HeatmapData(String timeSlot, Long ticketCount) {
        // Falls der String eine Zahl repräsentiert, wandeln wir ihn in einen Integer um.
        // Alternativ kannst du den String auch direkt speichern, wenn du ihn so brauchst.
        this.timeSlot = Integer.valueOf(timeSlot);
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
