package org.example.eventm.api.dtos;



import java.sql.Date;
import java.time.LocalDate;


public class TimeSeriesData {
    private LocalDate date;
    private Long ticketCount;

    public TimeSeriesData() {}

    // Falls du auch ein LocalDate direkt verarbeiten möchtest
    public TimeSeriesData(LocalDate date, Long ticketCount) {
        this.date = date;
        this.ticketCount = ticketCount;
    }

    // Neuer Konstruktor für java.sql.Date
    public TimeSeriesData(java.sql.Date date, Long ticketCount) {
        // Konvertieren in LocalDate
        this.date = date.toLocalDate();
        this.ticketCount = ticketCount;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getTicketCount() {
        return ticketCount;
    }
    public void setTicketCount(Long ticketCount) {
        this.ticketCount = ticketCount;
    }
}
