package io.axoniq.demo.bikerental.rental.command;

import io.axoniq.demo.bikerental.coreapi.rental.BikeStatus;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventsourcing.AggregateSnapshotter;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.extensions.mongo.MongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.spring.SpringMongoTemplate;
import org.axonframework.micrometer.GlobalMetricRegistry;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.stereotype.Repository;

@Configuration
public class SnapShotTriggerConfig {

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
