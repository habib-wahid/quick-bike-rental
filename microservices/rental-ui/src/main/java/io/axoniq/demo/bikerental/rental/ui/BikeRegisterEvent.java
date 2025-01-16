package io.axoniq.demo.bikerental.rental.ui;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.MetaData;

import java.time.Instant;
import java.util.Map;

public class BikeRegisterEvent implements EventMessage<Object> {

    private final String bikeId;
    private final String bikeType;
    private final String location;

    public BikeRegisterEvent(String bikeId, String bikeType, String location) {
        this.bikeId = bikeId;
        this.bikeType = bikeType;
        this.location = location;
    }

    public String getBikeId() {
        return bikeId;
    }

    public String getBikeType() {
        return bikeType;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String getIdentifier() {
        return bikeId;
    }

    @Override
    public MetaData getMetaData() {
        return MetaData.emptyInstance();
    }

    @Override
    public Object getPayload() {
        return null;
    }

    @Override
    public Class<Object> getPayloadType() {
        return null;
    }

    @Override
    public Instant getTimestamp() {
        return null;
    }

    @Override
    public EventMessage<Object> withMetaData(Map<String, ?> metaData) {
        return null;
    }

    @Override
    public EventMessage<Object> andMetaData(Map<String, ?> metaData) {
        return null;
    }
}
