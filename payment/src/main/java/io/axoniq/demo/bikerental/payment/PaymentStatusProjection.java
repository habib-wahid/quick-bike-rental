package io.axoniq.demo.bikerental.payment;

import io.axoniq.demo.bikerental.coreapi.payment.PaymentConfirmedEvent;
import io.axoniq.demo.bikerental.coreapi.payment.PaymentPreparedEvent;
import io.axoniq.demo.bikerental.coreapi.payment.PaymentRejectedEvent;
import io.axoniq.demo.bikerental.coreapi.payment.PaymentStatus;
import io.axoniq.demo.bikerental.coreapi.rental.BikeRegisteredEvent;
import io.axoniq.demo.bikerental.coreapi.rental.BikeStatus;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import static io.axoniq.demo.bikerental.coreapi.payment.PaymentStatus.Status.APPROVED;
import static io.axoniq.demo.bikerental.coreapi.payment.PaymentStatus.Status.PENDING;
import static io.axoniq.demo.bikerental.coreapi.payment.PaymentStatus.Status.REJECTED;

@ProcessingGroup("kafkaProcessor")
@Component
public class PaymentStatusProjection {

    private final PaymentStatusRepository paymentStatusRepository;
    private final QueryUpdateEmitter updateEmitter;

    public PaymentStatusProjection(PaymentStatusRepository paymentStatusRepository,
                                   QueryUpdateEmitter updateEmitter) {
        this.paymentStatusRepository = paymentStatusRepository;
        this.updateEmitter = updateEmitter;
    }

    @QueryHandler(queryName = "getStatus")
    public PaymentStatus getStatus(String paymentId) {
        return paymentStatusRepository.findById(paymentId).orElse(null);
    }

    @QueryHandler(queryName = "getPaymentId")
    public String getPaymentId(String paymentReference) {
        System.out.println("Get Payment Id: " + paymentReference);
        return paymentStatusRepository.findByReferenceAndStatus(paymentReference, PENDING).map(PaymentStatus::getId).orElse(null);
    }

    @QueryHandler(queryName = "getAllPayments")
    public Iterable<PaymentStatus> findByStatus(PaymentStatus.Status status) {
        if (status == null) {
            return paymentStatusRepository.findAll();
        }
        return paymentStatusRepository.findAllByStatus(status);
    }

    @QueryHandler(queryName = "getAllPayments")
    public Iterable<PaymentStatus> findAll() {
        return paymentStatusRepository.findAll();
    }

    @EventHandler
    public void handle(PaymentPreparedEvent event) {
        paymentStatusRepository.save(new PaymentStatus(event.paymentId(), event.amount(), event.paymentReference()));
        updateEmitter.emit(String.class, event.paymentReference()::equals, event.paymentId());
    }

    @EventHandler
    public void handle(PaymentConfirmedEvent event) {
        paymentStatusRepository.findById(event.paymentId()).ifPresent(s -> s.setStatus(APPROVED));
    }

    @EventHandler
    public void handle(PaymentRejectedEvent event) {
        paymentStatusRepository.findById(event.paymentId()).ifPresent(s -> s.setStatus(REJECTED));
    }


    @EventHandler  //<.>
    public void on(BikeRegisteredEvent event) { //<.>
        System.out.println("Bike registered event here " + event.getBikeId());
        var bikeStatus = new BikeStatus(event.getBikeId(), event.getBikeType(), event.getLocation()); //<.>
        //bikeStatusRepository.save(bikeStatus); //<.>
        //tag::UpdateEmitter[]
        updateEmitter.emit(q -> "findAll".equals(q.getQueryName()), bikeStatus); //<.>
        //end::UpdateEmitter[]
    }
}
