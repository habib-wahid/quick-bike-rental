package io.axoniq.demo.bikerental.coreapi.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "payment")
public class PaymentStatus {
    @Id
    private String id;

    private Status status;
    private int amount;
    private String reference;
    private String bikeId;

    public PaymentStatus() {
    }

    public PaymentStatus(String id, int amount, String reference) {
        this.id = id;
        this.amount = amount;
        this.reference = reference;
        this.status = Status.PENDING;

    }

    public PaymentStatus(String id, int amount, String reference, String bikeId) {
        this.id = id;
        this.amount = amount;
        this.reference = reference;
        this.status = Status.PENDING;
        this.bikeId = bikeId;
    }

    public String getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public String getBikeId() {
        return bikeId;
    }

    public Status getStatus() {
        return status;
    }

    public int getAmount() {
        return amount;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {

        PENDING, APPROVED, REJECTED
    }
}
