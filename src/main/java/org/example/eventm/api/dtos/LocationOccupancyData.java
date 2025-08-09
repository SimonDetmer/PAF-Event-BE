package org.example.eventm.api.dtos;


import java.math.BigInteger;

public class LocationOccupancyData {
    private Integer locationId;
    private String street;
    private Long ticketCount;
    private BigInteger capacity;

    public LocationOccupancyData() { }

    public LocationOccupancyData(Integer locationId, String street, Long ticketCount, BigInteger capacity) {
        this.locationId = locationId;
        this.street = street;
        this.ticketCount = ticketCount;
        this.capacity = capacity;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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
