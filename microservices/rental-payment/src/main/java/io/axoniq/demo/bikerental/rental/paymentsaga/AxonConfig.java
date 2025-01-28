package io.axoniq.demo.bikerental.rental.paymentsaga;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.spring.SpringMongoTemplate;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;

@Configuration
public class AxonConfig {


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
    public EventStorageEngine storageEngine(
            MongoDatabaseFactory factory,
            TransactionManager transactionManager,
            @Qualifier("eventSerializer") Serializer eventSerializer,
            @Qualifier("defaultSerializer") Serializer defaultSerializer) {
        return MongoEventStorageEngine.builder()
                .mongoTemplate(SpringMongoTemplate.builder()
                        .factory(factory)
                        .build())
                .eventSerializer(eventSerializer)
                .snapshotSerializer(defaultSerializer)
                .transactionManager(transactionManager)
                .build();
    }
}
