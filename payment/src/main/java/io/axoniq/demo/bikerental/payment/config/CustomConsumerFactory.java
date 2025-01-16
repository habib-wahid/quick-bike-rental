package io.axoniq.demo.bikerental.payment.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.axonframework.extensions.kafka.eventhandling.consumer.ConsumerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CustomConsumerFactory<K, V> implements ConsumerFactory<K, V> {
    @Override
    public Consumer<K, V> createConsumer(String groupId) {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        config.put("group.id", groupId != null ? groupId : "axon-consumer-group");
        config.put("enable.auto.commit", "false"); // Manual acknowledgment for precise handling

        return new KafkaConsumer<>(config);
    }
}
