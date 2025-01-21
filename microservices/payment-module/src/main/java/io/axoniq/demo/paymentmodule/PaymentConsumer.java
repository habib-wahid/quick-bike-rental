package io.axoniq.demo.paymentmodule;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.axoniq.demo.bikerental.coreapi.payment.PaymentStatus;
import io.axoniq.demo.bikerental.coreapi.rental.GeneralRentalEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class PaymentConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);
    private final ObjectMapper objectMapper;
    private final PaymentStatusRepository paymentStatusRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public PaymentConsumer(ObjectMapper objectMapper, PaymentStatusRepository paymentStatusRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.paymentStatusRepository = paymentStatusRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "bike-rent", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String event) throws JsonProcessingException {
        log.info("Payment Received Bike Rent event: {}", event);
        GeneralRentalEvent generalRentalEvent = objectMapper.readValue(event, GeneralRentalEvent.class);
        PaymentStatus paymentStatus = new PaymentStatus(UUID.randomUUID().toString(), randomAmount(), generalRentalEvent.getPaymentReference(), generalRentalEvent.getBikeId());
        paymentStatusRepository.save(paymentStatus);
    }

    private int randomAmount() {
        return ThreadLocalRandom.current().nextInt(10, 101);
    }
}
