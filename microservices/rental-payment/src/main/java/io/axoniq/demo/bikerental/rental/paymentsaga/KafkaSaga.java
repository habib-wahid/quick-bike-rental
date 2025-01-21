package io.axoniq.demo.bikerental.rental.paymentsaga;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.axoniq.demo.bikerental.coreapi.rental.GeneralRentalEvent;
import io.axoniq.demo.bikerental.coreapi.payment.PaymentDetailsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class KafkaSaga {

    private static final Logger log = LoggerFactory.getLogger(KafkaSaga.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaSaga(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "axon-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(byte[] event) throws JsonProcessingException {
        log.info("Saga Received event: {}", objectMapper.writeValueAsString(event));
        String bikeEvent = new String(event);
        GeneralRentalEvent generalRentalEvent = objectMapper.readValue(bikeEvent, GeneralRentalEvent.class);
       if (generalRentalEvent.getEventType().equalsIgnoreCase("BikeRent")) {
           kafkaTemplate.send("bike-rent", bikeEvent);
       } else if (generalRentalEvent.getEventType().equalsIgnoreCase("BikeRegistration")) {
           kafkaTemplate.send("bike-registration", bikeEvent);
       }
    }


    @KafkaListener(topics = "payment-details", groupId = "payment-details-group")
    public void paymentDetailsConsume(String event) {
        log.info("Saga Received Payment confirmation event: {}", event);
      kafkaTemplate.send("payment-confirmation-event-for-command", event);
    }
}
