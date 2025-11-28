package org.example.eventm.api.dtos;

import java.math.BigInteger;

public class LocationOccupancyData {

    private Integer locationId;
    private String name;
    private String city;
    private Long ticketCount;
    private BigInteger capacity;

    public LocationOccupancyData() {
    }

    public LocationOccupancyData(Integer locationId,
                                 String name,
                                 String city,
                                 Long ticketCount,
                                 BigInteger capacity) {
        this.locationId = locationId;
        this.name = name;
        this.city = city;
        this.ticketCount = ticketCount;
        this.capacity = capacity;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(Long ticketCount) {
        this.ticketCount = ticketCount;
    }

    public BigInteger getCapacity() {
        return capacity;
    }

    public void setCapacity(BigInteger capacity) {
        this.capacity = capacity;
    }
}
