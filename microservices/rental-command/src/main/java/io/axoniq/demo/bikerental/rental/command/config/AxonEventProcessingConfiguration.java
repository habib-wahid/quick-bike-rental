package io.axoniq.demo.bikerental.rental.command.config;

import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AxonEventProcessingConfiguration {

    @Autowired
    private KafkaEventPublisher<String, byte[]> kafkaEventPublisher;

    @Primary
    @Bean
    public EventProcessingConfigurer registerPublisherToEventProcessor(EventProcessingConfigurer eventProcessingConfigurer) {
        String processingGroup = KafkaEventPublisher.DEFAULT_PROCESSING_GROUP;
        eventProcessingConfigurer.registerEventHandler(configuration -> kafkaEventPublisher)
                .assignHandlerTypesMatching(
                        processingGroup,
                        clazz -> clazz.isAssignableFrom(KafkaEventPublisher.class)
                )
                .registerPooledStreamingEventProcessor(processingGroup);

        return eventProcessingConfigurer; // Returning the eventProcessingConfigurer bean
    }
}
