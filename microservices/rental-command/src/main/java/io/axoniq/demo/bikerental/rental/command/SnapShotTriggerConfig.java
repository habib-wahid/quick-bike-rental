package io.axoniq.demo.bikerental.rental.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.AggregateSnapshotter;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.extensions.mongo.MongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.extensions.mongo.spring.SpringMongoTemplate;
import org.axonframework.micrometer.GlobalMetricRegistry;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;

@Configuration
public class SnapShotTriggerConfig {


    @Bean
    public TokenStore tokenStore(
            MongoTemplate mongoTemplate,
            @Qualifier("defaultSerializer") Serializer serializer) {
        return MongoTokenStore.builder()
                .mongoTemplate(mongoTemplate)
                .serializer(serializer)
                .build();
    }

    @Bean
    public EventStore eventStore(EventStorageEngine storageEngine,
                                 GlobalMetricRegistry metricRegistry) {
        return EmbeddedEventStore.builder()
                .storageEngine(storageEngine)
                .messageMonitor(metricRegistry.registerEventBus("eventStore"))
                .build();
    }

    // The MongoEventStorageEngine stores each event in a separate MongoDB document.
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


    @Bean
    public Snapshotter snapshotter(EventStore eventStore) {
        return AggregateSnapshotter.builder()
                .eventStore(eventStore)
              //  .aggregateFactories(axonConfiguration.aggregateConfigurer())
                .build();
    }


//    @Bean
//    public Repository<BikeStatus> bikeRepository(MongoTemplate mongoTemplate) {
//        return GenericMongoRepository.builder(BikeStatus.class)
//                .mongoTemplate(mongoTemplate)
//                .build();
//    }
}
