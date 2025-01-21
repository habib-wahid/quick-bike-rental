package io.axoniq.demo.bikerental.rental.command.entity;

import io.axoniq.demo.bikerental.coreapi.rental.RentalStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "bike_collection")
public class BikeCollection {

    @Id //<.>
    //end::PersistenceIdAnnotation[]
    private String bikeId;
    private String bikeType;
    private String location;
    private String renter;
    private RentalStatus status;

    public BikeCollection() {
    }

    //end::Fields[]
    //tag::Constructor[]
    public  BikeCollection(String bikeId, String bikeType, String location) {
        this.bikeId = bikeId;
        this.bikeType = bikeType;
        this.location = location;
        this.status = RentalStatus.AVAILABLE;
    }

    //end::Constructor[]
    //tag::Methods[]
    //tag::Accessors[]
    // Accessor methods
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

    public RentalStatus getStatus() {
        return status;
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

    public void setStatus(RentalStatus status) {
        this.status = status;
    }
}
