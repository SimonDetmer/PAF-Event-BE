package org.example.eventm.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;


    @Column(name = "street", length = Integer.MAX_VALUE)
    @NotBlank
    private String street;


    @Column(name = "geo_x")
    @NotNull
    @Digits(integer = 3, fraction = 4)
    @Min(value = -180)
    @Max(value = 180)
    private BigDecimal geoX;

    @Column(name = "geo_y")
    @NotNull
    @Digits(integer = 3, fraction = 4)
    @Min(value = -90)
    @Max(value = 90)
    private BigDecimal geoY;


    @Column(name="capacity")
    @NotNull
    @PositiveOrZero
    @Max(value = 500_000)
    private BigInteger capacity;

    @OneToMany(mappedBy = "location", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JsonIgnore // Verhindert Serialization-Probleme
    private List<Event> events = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Getter und Setter
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }



    public BigInteger getCapacity() {
        return capacity;
    }

    public void setCapacity(BigInteger capacity) {
        this.capacity = capacity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public BigDecimal getGeoX() {
        return geoX;
    }

    public void setGeoX(BigDecimal geoX) {
        this.geoX = geoX;
    }

    public BigDecimal getGeoY() {
        return geoY;
    }

    public void setGeoY(BigDecimal geoY) {
        this.geoY = geoY;
    }

}