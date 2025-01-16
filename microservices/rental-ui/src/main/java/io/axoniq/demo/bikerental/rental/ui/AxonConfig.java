package io.axoniq.demo.bikerental.rental.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.producer.*;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.spring.SpringMongoTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.MongoDatabaseFactory;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Configuration
public class AxonConfig {
    @Bean
    public EventStorageEngine storageEngine(MongoDatabaseFactory factory,
                                            TransactionManager transactionManager) {
        return MongoEventStorageEngine.builder()
                .mongoTemplate(SpringMongoTemplate.builder()
                        .factory(factory)
                        .build())
                .transactionManager(transactionManager)
                // ...
                .build();
    }


    @Bean
    public KafkaMessageConverter<String, byte[]> kafkaMessageConverter(ObjectMapper objectMapper) {
        return new KafkaMessageConverter<String, byte[]>() {

            @Override
            public ProducerRecord<String, byte[]> createKafkaMessage(EventMessage<?> eventMessage, String topic) {
                try {
                    // Serialize the payload of the EventMessage to a byte array
                    byte[] payload = objectMapper.writeValueAsBytes(eventMessage.getPayload());

                    // Return the Kafka ProducerRecord with the serialized payload
                    return new ProducerRecord<>(topic, eventMessage.getIdentifier(), payload);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to serialize event payload", e);
                }
            }

            @Override
            public Optional<EventMessage<?>> readKafkaMessage(ConsumerRecord<String, byte[]> consumerRecord) {
                return Optional.empty();
            }
        };
    }

    @Bean
    public ProducerFactory<String, byte[]> producerFactory() {
        return DefaultProducerFactory.<String, byte[]>builder()
                .closeTimeout(Duration.ofSeconds(30))
                .producerCacheSize(10)
                .configuration(Map.of(
                        "bootstrap.servers", "localhost:9092",
                        "key.serializer", "org.apache.kafka.common.serialization.StringSerializer",
                        "value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer"
                ))
                .confirmationMode(ConfirmationMode.NONE)
                .transactionalIdPrefix("my-transaction-")
                .build();
    }



    @Bean
    public KafkaPublisher<String, byte[]> kafkaPublisher(
            ProducerFactory<String, byte[]> producerFactory,
            KafkaMessageConverter<String, byte[]> kafkaMessageConverter) {
        return KafkaPublisher.<String, byte[]>builder()
                .topicResolver(m -> Optional.of("axon-events"))
                .producerFactory(producerFactory)
                .messageConverter(kafkaMessageConverter)
                .publisherAckTimeout(1000)
                .build();
    }

    @Bean
    public KafkaEventPublisher<String, byte[]> kafkaEventPublisher(
            KafkaPublisher<String, byte[]> kafkaPublisher) {
        return KafkaEventPublisher.<String, byte[]>builder()
                .kafkaPublisher(kafkaPublisher)
                .build();
    }

}
