package io.axoniq.demo.bikerental.rental.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.axoniq.demo.bikerental.coreapi.payment.PaymentDetailsEvent;
import io.axoniq.demo.bikerental.coreapi.payment.PaymentStatus;
import io.axoniq.demo.bikerental.coreapi.rental.BikeStatus;
import io.axoniq.demo.bikerental.coreapi.rental.RentalStatus;
import io.axoniq.demo.bikerental.rental.command.entity.BikeCollection;
import io.axoniq.demo.bikerental.rental.command.repository.BikeCollectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class Consumer {


    private static final Logger log = LoggerFactory.getLogger(Consumer.class);
    private final ObjectMapper objectMapper;
    private final BikeCollectionRepository bikeCollectionRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public Consumer(ObjectMapper objectMapper, BikeCollectionRepository bikeCollectionRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.bikeCollectionRepository = bikeCollectionRepository;
        this.kafkaTemplate = kafkaTemplate;
    }


    @KafkaListener(topics = "payment-confirmation-event-for-command", groupId = "payment-confirmation-command-group")
    public void consume(String paymentConfirmationEvent) throws JsonProcessingException {
        log.info("Command Received Payment Confirmation: {}", paymentConfirmationEvent);
        PaymentDetailsEvent paymentDetailsEvent = objectMapper.readValue(paymentConfirmationEvent, PaymentDetailsEvent.class);
        if (paymentDetailsEvent != null && paymentDetailsEvent.getPaymentStatus().equals(PaymentDetailsEvent.PaymentApproveStatus.APPROVED)) {
            BikeCollection bikeCollection = bikeCollectionRepository.findById(paymentDetailsEvent.getBikeId()).get();
            bikeCollection.setStatus(RentalStatus.RENTED);
            bikeCollectionRepository.save(bikeCollection);
            kafkaTemplate.send("payment-confirmation-event-for-query", paymentConfirmationEvent);
        } else {
            BikeCollection bikeCollection = bikeCollectionRepository.findById(paymentDetailsEvent.getBikeId()).get();
            bikeCollection.setStatus(RentalStatus.AVAILABLE);
            bikeCollection.setRenter(null);
            bikeCollectionRepository.save(bikeCollection);
            kafkaTemplate.send("payment-confirmation-event-for-query", paymentConfirmationEvent);
        }
    }
}
