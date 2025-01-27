package io.axoniq.demo.bikerental.rental.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.extensions.mongo.MongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.extensions.mongo.spring.SpringMongoTemplate;
import org.axonframework.messaging.StreamableMessageSource;
import org.axonframework.queryhandling.QueryBus;
import org.axonframework.queryhandling.SimpleQueryBus;
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
    public TokenStore tokenStore(
            MongoTemplate mongoTemplate,
            @Qualifier("defaultSerializer") Serializer serializer) {
        return MongoTokenStore.builder()
                .mongoTemplate(mongoTemplate)
                .serializer(serializer)
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
//    @Bean
//    public ConfigurerModule initialTrackingTokenConfigurerModule() {
//        TrackingEventProcessorConfiguration tepConfig =
//                TrackingEventProcessorConfiguration.forSingleThreadedProcessing()
//                        .andInitialTrackingToken(StreamableMessageSource::createTailToken);
//
//        return configurer -> configurer.eventProcessing(
//                processingConfigurer -> processingConfigurer.registerTrackingEventProcessorConfiguration(
//                        "my-processor", config -> tepConfig
//                )
//        );
//    }
}
