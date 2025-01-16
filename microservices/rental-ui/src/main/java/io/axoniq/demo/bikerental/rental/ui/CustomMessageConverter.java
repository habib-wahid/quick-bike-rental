package io.axoniq.demo.bikerental.rental.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;

import java.util.Optional;

public class CustomMessageConverter  {

    private static final ObjectMapper objectMapper = new ObjectMapper();


//    public CustomMessageConverter(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }

    public static ProducerRecord<String, byte[]> createKafkaMessage(EventMessage<?> eventMessage, String topic) {
        try {
            // Serialize the payload to byte[]
            byte[] payload = objectMapper.writeValueAsBytes(eventMessage.getPayload());

            // Create and return a ProducerRecord with String key and byte[] value
            return new ProducerRecord<>(topic, eventMessage.getIdentifier(), payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event payload", e);
        }
    }
}
