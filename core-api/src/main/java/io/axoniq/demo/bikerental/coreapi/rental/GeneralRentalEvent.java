package io.axoniq.demo.bikerental.coreapi.rental;

public class GeneralRentalEvent {
    private String eventType;
    private String bikeId;
    private String bikeType;
    private String location;
    private String renter;
    private String rentalReference;
    private String paymentReference;
    private Boolean paymentFailed;

    public GeneralRentalEvent() {}
    public GeneralRentalEvent(String eventType,String bikeId, String bikeType, String location, String renter, String rentalReference) {
        this.eventType = eventType;
        this.bikeId = bikeId;
        this.bikeType = bikeType;
        this.location = location;
        this.renter = renter;
        this.rentalReference = rentalReference;
    }

    public GeneralRentalEvent(String eventType,String bikeId, String bikeType, String location, String renter, String rentalReference, String paymentReference, Boolean paymentFailed) {
        this.eventType = eventType;
        this.bikeId = bikeId;
        this.bikeType = bikeType;
        this.location = location;
        this.renter = renter;
        this.rentalReference = rentalReference;
        this.paymentReference = paymentReference;
        this.paymentFailed = paymentFailed;
    }

    public String getEventType() {
        return eventType;
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

    public String getRenter() {
        return renter;
    }

    public String getRentalReference() {
        return rentalReference;
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

    public void setRenter(String renter) {
        this.renter = renter;
    }

    public void setRentalReference(String rentalReference) {
        this.rentalReference = rentalReference;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public Boolean getPaymentFailed() {
        return paymentFailed;
    }

    public void setPaymentFailed(Boolean paymentFailed) {
        this.paymentFailed = paymentFailed;
    }
}
