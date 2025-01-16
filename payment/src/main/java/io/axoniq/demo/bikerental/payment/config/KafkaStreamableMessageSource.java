package io.axoniq.demo.bikerental.payment.config;

import org.axonframework.common.stream.BlockingStream;
import org.axonframework.eventhandling.TrackingToken;
import org.axonframework.messaging.StreamableMessageSource;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.extensions.kafka.eventhandling.consumer.Fetcher;

import java.time.Duration;
import java.time.Instant;

public class KafkaStreamableMessageSource implements StreamableMessageSource<TrackedEventMessage<?>> {

    private final Fetcher fetcher;
    private final String topic;

    public KafkaStreamableMessageSource(Fetcher fetcher, String topic) {
        this.fetcher = fetcher;
        this.topic = topic;
    }

//    @Override
//    public Stream<TrackedEventMessage<?>> openStream(TrackingToken trackingToken) {
//        KafkaTrackingToken kafkaTrackingToken = (KafkaTrackingToken) trackingToken;
//        return fetcher.fetch(kafkaTrackingToken, topic).stream();
//    }

    @Override
    public TrackingToken createTailToken() {
        return StreamableMessageSource.super.createTailToken();
    }

    @Override
    public TrackingToken createHeadToken() {
        return StreamableMessageSource.super.createHeadToken();
    }

    @Override
    public TrackingToken createTokenAt(Instant dateTime) {
        return StreamableMessageSource.super.createTokenAt(dateTime);
    }

    @Override
    public TrackingToken createTokenSince(Duration duration) {
        return StreamableMessageSource.super.createTokenSince(duration);
    }

    @Override
    public BlockingStream<TrackedEventMessage<?>> openStream(TrackingToken trackingToken) {
        return null;
    }
}