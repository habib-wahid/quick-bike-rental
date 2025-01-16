package io.axoniq.demo.bikerental.coreapi.rental;

public class BikeRequestedEvent {
    private String bikeId;
    private String renter;
    private String rentalReference;

    public BikeRequestedEvent(String bikeId, String renter, String rentalReference) {
        this.bikeId = bikeId;
        this.renter = renter;
        this.rentalReference = rentalReference;
    }

    public void setBikeId(String bikeId) {
        this.bikeId = bikeId;
    }

    public void setRenter(String renter) {
        this.renter = renter;
    }

    public void setRentalReference(String rentalReference) {
        this.rentalReference = rentalReference;
    }

    public String getBikeId() {
        return bikeId;
    }

    public String getRenter() {
        return renter;
    }

    public String getRentalReference() {
        return rentalReference;
    }
}

