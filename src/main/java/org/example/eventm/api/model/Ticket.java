package org.example.eventm.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "price")
    @NotNull
    @Digits(integer = 10, fraction = 2)
    @PositiveOrZero
    private BigDecimal price;

    // Beziehung zu Event (falls weiterhin benötigt)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @JsonBackReference  // Verhindert rekursive Serialisierung, wenn Event seine Tickets serialisiert.
    @NotNull
    private Event event;

    // Beziehung zu Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore  // Damit wird das Order-Objekt nicht vollständig mit ausgegeben.
    private Order order;

    // Standard-Konstruktor
    public Ticket() {}

    public Ticket(BigDecimal price, Event event, Order order) {
        this.price = price;
        this.event = event;
        this.order = order;
    }

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Getter und Setter
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Getter und Setter


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    // Neuer Getter, der nur die order_id ausgibt.
    @JsonProperty("order_id")
    public Integer getOrderId() {
        return order != null ? order.getId() : null;
    }
}
