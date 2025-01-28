package io.axoniq.demo.paymentmodule;

import io.axoniq.demo.bikerental.coreapi.payment.PaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentStatusRepository extends MongoRepository<PaymentStatus, String> {
    PaymentStatus findByReference(String paymentId);
    PaymentStatus findByBikeId(String bikeId);
}
