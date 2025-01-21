package io.axoniq.demo.bikerental.rental.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.axoniq.demo.bikerental.coreapi.payment.PaymentDetailsEvent;
import io.axoniq.demo.bikerental.coreapi.rental.BikeRegisteredEvent;
import io.axoniq.demo.bikerental.coreapi.rental.BikeStatus;
import io.axoniq.demo.bikerental.coreapi.rental.GeneralRentalEvent;
import io.axoniq.demo.bikerental.coreapi.rental.RentalStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    private final BikeStatusRepository bikeStatusRepository;
    private final ObjectMapper objectMapper;

    public KafkaConsumer(BikeStatusRepository bikeStatusRepository, ObjectMapper objectMapper) {
        this.bikeStatusRepository = bikeStatusRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "payment-confirmation-event-for-query", groupId = "query-confirmation-group")
    public void consume(String event) throws JsonProcessingException {
        log.info("Query Received payment confirmation: {}", event);
        PaymentDetailsEvent paymentDetailsEvent = objectMapper.readValue(event, PaymentDetailsEvent.class);
        if (paymentDetailsEvent != null && paymentDetailsEvent.getPaymentStatus().equals(PaymentDetailsEvent.PaymentApproveStatus.APPROVED)) {
            BikeStatus bikeStatus = bikeStatusRepository.findByBikeId(paymentDetailsEvent.getBikeId());
            bikeStatus.setStatus(RentalStatus.RENTED);
            bikeStatusRepository.save(bikeStatus);
        } else {
            assert paymentDetailsEvent != null;
            BikeStatus bikeStatus = bikeStatusRepository.findByBikeId(paymentDetailsEvent.getBikeId());
            bikeStatusRepository.delete(bikeStatus);
        }
    }

    @KafkaListener(topics = "bike-registration", groupId = "bike-registration-group")
    public void consumeBikeRegistration(String event) throws JsonProcessingException {
        log.info("Query Bike Registration event received: {}", event);
       GeneralRentalEvent generalRentalEvent = objectMapper.readValue(event, GeneralRentalEvent.class);
       BikeStatus bikeStatus = new BikeStatus(generalRentalEvent.getBikeId(), generalRentalEvent.getBikeType(), generalRentalEvent.getLocation());
       bikeStatusRepository.save(bikeStatus);
    }


    @KafkaListener(topics = "bike-rent", groupId = "bike-rent-group")
    public void consumeBikeRent(String event) throws JsonProcessingException {
        log.info("Query Bike Rent event received: {}", event);
        GeneralRentalEvent generalRentalEvent = objectMapper.readValue(event, GeneralRentalEvent.class);
        BikeStatus bikeStatus = bikeStatusRepository.findByBikeId(generalRentalEvent.getBikeId());
        bikeStatus.setStatus(RentalStatus.REQUESTED);
        bikeStatusRepository.save(bikeStatus);
    }
}
