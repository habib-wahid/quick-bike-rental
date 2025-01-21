package io.axoniq.demo.bikerental.rental.query;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.Configurer;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.extensions.kafka.configuration.KafkaMessageSourceConfigurer;
import org.axonframework.extensions.kafka.eventhandling.DefaultKafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.consumer.AsyncFetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.ConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.DefaultConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.Fetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.streamable.KafkaEventMessage;
import org.axonframework.extensions.kafka.eventhandling.consumer.streamable.SortedKafkaMessageBuffer;
import org.axonframework.extensions.kafka.eventhandling.consumer.streamable.StreamableKafkaMessageSource;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.spring.SpringMongoTemplate;
import org.axonframework.messaging.StreamableMessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.MongoDatabaseFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

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
//
//    @Bean
//    public KafkaMessageSource<String, byte[]> kafkaMessageSource(
//            DefaultKafkaConsumerFactory<String, byte[]> consumerFactory) {
//        return KafkaMessageSource.builder()
//                .consumerFactory(consumerFactory)
//                .topics("your-event-topic")
//                .build();
//    }

//    @Bean
//    public ConsumerFactory<String, byte[]> consumerFactory(Map<String, Object> consumerConfiguration) {
//
//
//        consumerConfiguration.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Kafka broker
//        consumerConfiguration.put(ConsumerConfig.GROUP_ID_CONFIG, "axon-events-group"); // Consumer group ID
//        consumerConfiguration.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // Key deserializer
//        consumerConfiguration.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class); // Value deserializer
//        consumerConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Start from the beginning if no offset
//
//        return new DefaultConsumerFactory<>(consumerConfiguration);
//    }


//    @Bean
//    public Fetcher<?, ?, ?> fetcher(
//            ExecutorService executorService) {
//        return AsyncFetcher.builder()
//                .pollTimeout(5000)          // Defaults to "5000" milliseconds
//                .executorService(executorService)    // Defaults to a cached thread pool executor
//                .build();
//    }
//
//    // ...
//    @Bean
//    public KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer(Configurer configurer) {
//        KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer = new KafkaMessageSourceConfigurer();
//        configurer.registerModule(kafkaMessageSourceConfigurer);
//        return kafkaMessageSourceConfigurer;
//    }
//
//
//    @Lazy
//    @Bean
//    public StreamableKafkaMessageSource<String, byte[]> streamableKafkaMessageSource(List<String> topics,
//                                                                                     ConsumerFactory<String, byte[]> consumerFactory,
//                                                                                     Fetcher<String, byte[], KafkaEventMessage> fetcher
//                                                                                   //  KafkaMessageConverter<String, byte[]> messageConverter
//                                                                                    ) {
//
//        return StreamableKafkaMessageSource.<String, byte[]>builder()
//                .topics(List.of("axon-events"))                                                 // Defaults to a collection of "Axon.Events"
//                .consumerFactory(consumerFactory)                               // Hard requirement
//                .fetcher(fetcher)                                               // Hard requirement
//              //  .messageConverter(messageConverter)                             // Defaults to a "DefaultKafkaMessageConverter"
//                .bufferFactory(
//                        () -> new SortedKafkaMessageBuffer<>(1000))   // Defaults to a "SortedKafkaMessageBuffer" with a buffer capacity of "1000"
//                .build();
//    }
//
//    @Bean
//    public ConfigurerModule trackingProcessorConfigurerModule(StreamableKafkaMessageSource<String , byte[]> streamableKafkaMessageSource) {
//        // This configuration object allows for fine-grained control over the Tracking Processor
//        TrackingEventProcessorConfiguration tepConfig =
//                TrackingEventProcessorConfiguration.forSingleThreadedProcessing();
//
//        return configurer -> configurer.eventProcessing(processingConfigurer -> processingConfigurer
//                // To configure a processor to be tracking ...
//                .registerTrackingEventProcessor("my-processor")
//                // ... to define a specific StreamableMessageSource ...
//                .registerTrackingEventProcessor(
//                        "my-processor",
//                        conf -> streamableKafkaMessageSource)
//        );
//    }


//    @Bean
//    public void configureEventProcessing(EventProcessingConfigurer configurer,
//                                         KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer) {
//        configurer.registerTrackingEventProcessor(
//                "query-service-processor", // Name of your processor
//                configuration -> kafkaMessageSourceConfigurer.configureSubscribableSource("your-kafka-topic")
//        );
//    }
//
//
    @Bean
    public ConfigurerModule initialTrackingTokenConfigurerModule() {
        TrackingEventProcessorConfiguration tepConfig =
                TrackingEventProcessorConfiguration.forSingleThreadedProcessing()
                        .andInitialTrackingToken(StreamableMessageSource::createTailToken);

        return configurer -> configurer.eventProcessing(
                processingConfigurer -> processingConfigurer.registerTrackingEventProcessorConfiguration(
                        "my-processor", config -> tepConfig
                )
        );
    }
}
