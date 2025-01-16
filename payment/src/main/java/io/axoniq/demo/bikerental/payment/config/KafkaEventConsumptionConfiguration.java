package io.axoniq.demo.bikerental.payment.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.axonframework.config.Configurer;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.extensions.kafka.configuration.KafkaMessageSourceConfigurer;
import org.axonframework.extensions.kafka.eventhandling.DefaultKafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.consumer.AsyncFetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.ConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.DefaultConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.Fetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.streamable.StreamableKafkaMessageSource;
import org.axonframework.extensions.kafka.eventhandling.consumer.subscribable.SubscribableKafkaMessageSource;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaEventPublisher;
import org.axonframework.messaging.StreamableMessageSource;
import org.axonframework.serialization.Serializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import java.util.*;
import java.util.concurrent.ExecutorService;

@Configuration
public class KafkaEventConsumptionConfiguration {

//    @Bean
//    public ConsumerFactory<String, byte[]> consumerFactory() {
//        return new CustomConsumerFactory<>();
//    }
//
//    @Bean
//    public KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer() {
//        return new KafkaMessageSourceConfigurer();
//    }
//
//    @Bean
//    public SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource(
//            List<String> topics,
//            ConsumerFactory<String, byte[]> consumerFactory,
//            Fetcher<String, byte[], EventMessage<?>> fetcher,
//            KafkaMessageConverter<String, byte[]> messageConverter,
//            KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer) {
//
//        List<String> topicList = new ArrayList<>();
//        topicList.add("axon-events");
//        SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource =
//                SubscribableKafkaMessageSource.<String, byte[]>builder()
//                        .topics(topicList) // Defaults to "Axon.Events"
//                        .groupId("axon-consumer-group")
//                        .consumerFactory(consumerFactory)
//                        .fetcher(fetcher)
//                        .messageConverter(messageConverter)
//                        .build();
//
//        kafkaMessageSourceConfigurer.configureSubscribableSource(
//                configuration -> subscribableKafkaMessageSource);
//        return subscribableKafkaMessageSource;
//    }
//
//
//
//
//    @Primary
//    @Bean
//    public EventProcessingConfigurer configureSubscribableKafkaSource(EventProcessingConfigurer eventProcessingConfigurer,
//                                                 SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource) {
//        return eventProcessingConfigurer.registerSubscribingEventProcessor(
//                "kafkaProcessor",
//                configuration -> subscribableKafkaMessageSource
//        );
//    }


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
