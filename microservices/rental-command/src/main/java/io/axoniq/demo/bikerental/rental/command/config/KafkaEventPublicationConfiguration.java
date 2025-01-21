package io.axoniq.demo.bikerental.rental.command.config;

import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.producer.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Configuration
public class KafkaEventPublicationConfiguration {


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
                .confirmationMode(ConfirmationMode.WAIT_FOR_ACK)
                .transactionalIdPrefix("my-transaction-")
                .build();
    }



    @Lazy
    @Bean
    public KafkaPublisher<String, byte[]> kafkaPublisher(ProducerFactory<String, byte[]> producerFactory,
                                                         KafkaMessageConverter<String, byte[]> kafkaMessageConverter) {
        return KafkaPublisher.<String, byte[]>builder()
                .topicResolver(m -> Optional.of("axon-events"))
                .producerFactory(producerFactory)
                .messageConverter(kafkaMessageConverter)
                .publisherAckTimeout(1000)
                .build();
    }

    @Bean
    public KafkaEventPublisher<String, byte[]> kafkaEventPublisher(KafkaPublisher<String, byte[]> kafkaPublisher) {
        return KafkaEventPublisher.<String, byte[]>builder()
                .kafkaPublisher(kafkaPublisher)
                .build();
    }


}