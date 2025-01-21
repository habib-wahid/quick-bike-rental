package io.axoniq.demo.bikerental.rental.command.dto;

import org.springframework.http.HttpStatus;

public class RentalResponse {
    private String bikeId;
    private String status;

    public RentalResponse(String bikeId, String status) {
        this.bikeId = bikeId;
        this.status = status;
    }

    public String getBikeId() {
        return bikeId;
    }

    public String getStatus() {
        return status;
    }

    public void setBikeId(String bikeId) {
        this.bikeId = bikeId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
