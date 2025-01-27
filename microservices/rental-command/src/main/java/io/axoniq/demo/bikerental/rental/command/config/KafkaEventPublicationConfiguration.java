package io.axoniq.demo.bikerental.rental.command.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.extensions.kafka.eventhandling.DefaultKafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.producer.*;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Configuration
public class KafkaEventPublicationConfiguration {


    @Bean
    public ObjectMapper objectMapper() {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();

        return new ObjectMapper()
                .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
    }

    @Bean
    @Primary
    public Serializer defaultSerializer(ObjectMapper objectMapper) {
        return JacksonSerializer.builder()
                .objectMapper(objectMapper.copy())
                .lenientDeserialization()
                .build();
    }

    @Bean(name = "eventSerializer")
    public Serializer eventSerializer(ObjectMapper objectMapper) {
        return JacksonSerializer.builder()
                .objectMapper(objectMapper.copy())
                .lenientDeserialization()
                .build();
    }

    @Bean(name = "messageSerializer")
    public Serializer messageSerializer(ObjectMapper objectMapper) {
        return JacksonSerializer.builder()
                .objectMapper(objectMapper.copy())
                .lenientDeserialization()
                .build();
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
                .confirmationMode(ConfirmationMode.WAIT_FOR_ACK)
                .transactionalIdPrefix("my-transaction-")
                .build();
    }


    @Bean
    public KafkaMessageConverter<String, byte[]> kafkaMessageConverter(
            @Qualifier("eventSerializer") Serializer eventSerializer) {
        return DefaultKafkaMessageConverter.builder()
                .serializer(eventSerializer)
                .build();
    }

    @Bean
    @DependsOn("kafkaMessageConverter")
    public KafkaPublisher<String, byte[]> kafkaPublisher(ProducerFactory<String, byte[]> producerFactory,
                                                         KafkaMessageConverter<String, byte[]> kafkaMessageConverter,
                                                         @Qualifier("eventSerializer") Serializer eventSerializer
                                                         ) {
        return KafkaPublisher.<String, byte[]>builder()
                .topicResolver(m -> Optional.of("axon-events"))
                .producerFactory(producerFactory)
                .messageConverter(kafkaMessageConverter)
                .serializer(eventSerializer)
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