package io.axoniq.demo.bikerental.coreapi.rental;


public class BikeRegisteredEvent {
    private String bikeId;
    private String bikeType;
    private String location;

    public BikeRegisteredEvent() {

    }
    public BikeRegisteredEvent(String bikeId, String bikeType, String location) {
        this.bikeId = bikeId;
        this.bikeType = bikeType;
        this.location = location;
    }

    public void setBikeId(String bikeId) {
        this.bikeId = bikeId;
    }

    public void setBikeType(String bikeType) {
        this.bikeType = bikeType;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBikeId() {
        return bikeId;
    }

    public String getBikeType() {
        return bikeType;
    }

    public String getLocation() {
        return location;
    }
}

