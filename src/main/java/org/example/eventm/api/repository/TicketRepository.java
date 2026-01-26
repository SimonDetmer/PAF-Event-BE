package org.example.eventm.api.repository;

import org.example.eventm.api.dtos.EventSummaryData;
import org.example.eventm.api.dtos.HeatmapData;
import org.example.eventm.api.dtos.LocationOccupancyData;
import org.example.eventm.api.dtos.TimeSeriesData;
import org.example.eventm.api.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    void deleteAllByEventId(Integer eventId);

    // 1. Zeitlicher Verlauf der Ticket-Verkäufe (z. B. pro Tag)
    @Query("SELECT new org.example.eventm.api.dtos.TimeSeriesData(" +
            "CAST(t.order.createdAt as date), COUNT(t)) " +
            "FROM Ticket t " +
            "WHERE t.order IS NOT NULL " +
            "GROUP BY CAST(t.order.createdAt as date) " +
            "ORDER BY CAST(t.order.createdAt as date)")
    List<TimeSeriesData> findTicketSalesOverTime();


    // 2. Ticket-Verkäufe pro Event
    @Query("SELECT new org.example.eventm.api.dtos.EventSummaryData(" +
            "e.id, e.title, COUNT(t), SUM(t.price)) " +
            "FROM Ticket t JOIN t.event e " +
            "GROUP BY e.id, e.title")
    List<EventSummaryData> findTicketSalesPerEvent();

    // 3. Event Summaries: Ticket-Zahl und Umsatz pro Event
    @Query("SELECT new org.example.eventm.api.dtos.EventSummaryData(" +
            "e.id, e.title, COUNT(t), SUM(t.price)) " +
            "FROM Ticket t JOIN t.event e " +
            "WHERE t.order IS NOT NULL " +
            "GROUP BY e.id, e.title")
    List<EventSummaryData> findEventSummaries();


    // 4. Heatmap-Daten
    @Query("SELECT new org.example.eventm.api.dtos.HeatmapData(" +
            "CAST(FUNCTION('date_part', 'hour', t.order.createdAt) as integer), " +
            "COUNT(t)) " +
            "FROM Ticket t " +
            "WHERE t.order IS NOT NULL " +
            "GROUP BY CAST(FUNCTION('date_part', 'hour', t.order.createdAt) as integer) " +
            "ORDER BY CAST(FUNCTION('date_part', 'hour', t.order.createdAt) as integer)")
    List<HeatmapData> findBookingHeatmapData();

    // 5. Location-Auslastung
    @Query("SELECT new org.example.eventm.api.dtos.LocationOccupancyData(" +
            "l.id, l.name, l.city, COUNT(t), l.capacity) " +
            "FROM Ticket t JOIN t.event e JOIN e.location l " +
            "GROUP BY l.id, l.name, l.city, l.capacity")
    List<LocationOccupancyData> findLocationOccupancyData();
}
