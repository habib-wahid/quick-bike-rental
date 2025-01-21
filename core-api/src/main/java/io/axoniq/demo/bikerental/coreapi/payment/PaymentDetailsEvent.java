package io.axoniq.demo.bikerental.coreapi.payment;

public class PaymentDetailsEvent {
    private String bikeId;
    private String paymentId;
    private PaymentApproveStatus paymentStatus;

    public PaymentDetailsEvent(String bikeId,String paymentId, PaymentApproveStatus paymentStatus) {
        this.bikeId = bikeId;
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentApproveStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentApproveStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getBikeId() {
        return bikeId;
    }

    public void setBikeId(String bikeId) {
        this.bikeId = bikeId;
    }

    public enum PaymentApproveStatus {
        APPROVED,
        FAILED,
    }
}
