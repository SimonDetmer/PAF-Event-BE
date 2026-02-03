package org.example.eventm.api.dto;



import java.time.LocalDate;


public class TimeSeriesData {
    private LocalDate date;
    private Long ticketCount;

    public TimeSeriesData() {}

    public TimeSeriesData(LocalDate date, Long ticketCount) {
        this.date = date;
        this.ticketCount = ticketCount;
    }

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
