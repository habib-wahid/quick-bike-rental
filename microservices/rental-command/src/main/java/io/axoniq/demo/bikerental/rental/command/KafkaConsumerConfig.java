package io.axoniq.demo.bikerental.rental.command;

import org.axonframework.config.ConfigurerModule;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.messaging.StreamableMessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConsumerConfig {
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
