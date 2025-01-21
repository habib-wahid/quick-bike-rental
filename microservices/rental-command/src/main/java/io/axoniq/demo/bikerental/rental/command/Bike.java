package io.axoniq.demo.bikerental.rental.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.axoniq.demo.bikerental.coreapi.rental.*;
import io.axoniq.demo.bikerental.rental.command.repository.BikeCollectionRepository;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

//tag::SnapshotTriggerDefinition[]
@Aggregate(snapshotTriggerDefinition = "bikeSnapshotDefinition") //<.>
//end::SnapshotTriggerDefinition[]
//tag::BikeAggregateClass[]
public class Bike {


    private static final Logger log = LoggerFactory.getLogger(Bike.class);
    //tag::BikeAggregateFields[]
    @AggregateIdentifier //<.>
    private String bikeId;

    private boolean isAvailable;
    private String reservedBy;
    private boolean reservationConfirmed;

    public Bike() {

    }
    //end::BikeAggregateFields[]
    //tag::JsonCreator[]
    /* Constructor used to reconstruct the aggregate from a JSON based snapshot with Jackson */
    @JsonCreator
    public Bike(@JsonProperty("bikeId") String bikeId,
                @JsonProperty("available") boolean isAvailable,
                @JsonProperty("reservedBy") String reservedBy,
                @JsonProperty("reservationConfirmed") boolean reservationConfirmed
    ) {
        this.bikeId = bikeId;
        this.isAvailable = isAvailable;
        this.reservedBy = reservedBy;
        this.reservationConfirmed = reservationConfirmed;
    }
    //end::JsonCreator[]

    //tag::RegisterBikeCommandHandler[]
    @CommandHandler //<.>
    public Bike(RegisterBikeCommand command) {
      //  this.bikeRepository = bikeRepository; //<.>
        log.info("Here bike command " + command);
        var seconds = Instant.now().getEpochSecond();
        if (seconds % 5 ==0) {
            throw new IllegalStateException("Can't accept new bikes right now");
        }

        this.bikeId = command.bikeId();
        this.isAvailable = true;

        apply(new GeneralRentalEvent("BikeRegistration", command.bikeId(), command.bikeType(), command.location(), "", "")); //<.>
    }

    @CommandHandler
    public String handle(RequestBikeCommand command) {
        System.out.println("Bikes");
        String rentalReference = UUID.randomUUID().toString();
        this.reservedBy = command.renter();
        this.reservationConfirmed = false;
        this.isAvailable = false;
        String paymentReference = UUID.randomUUID().toString();
        apply(new GeneralRentalEvent("BikeRent",command.bikeId(), "", "", command.renter(), rentalReference, paymentReference, false));

        return paymentReference;
    }

    //end::RequestBikeCommandHandler[]
    //tag::ApproveRequestCommandHandler[]
    @CommandHandler
    public void handle(ApproveRequestCommand command) {
        if (!Objects.equals(reservedBy, command.renter())
                || reservationConfirmed) {
            return ;
        }
        apply(new BikeInUseEvent(command.bikeId(), command.renter()));
    }

    //end::ApproveRequestCommandHandler[]
    //tag::RejectRequestCommandHandler[]
    @CommandHandler
    public void handle(RejectRequestCommand command) {
        if (!Objects.equals(reservedBy, command.renter())
                || reservationConfirmed) {
            return;
        }
        apply(new RequestRejectedEvent(command.bikeId()));
    }

    //end::RejectRequestCommandHandler[]
    //tag::ReturnBikeCommandHandler[]
    @CommandHandler
    public void handle(ReturnBikeCommand command) {
        if (this.isAvailable) {
            throw new IllegalStateException("Bike was already returned");
        }
        apply(new BikeReturnedEvent(command.bikeId(), command.location()));
    }

    //end::ReturnBikeCommandHandler[]
    //tag::BikeRegisteredEventSourcingHandler[]
    @EventSourcingHandler //<.>
    protected void handle(BikeRegisteredEvent event) { //<.>
        this.bikeId = event.getBikeId();
        this.isAvailable = true;
    }

    //end::BikeRegisteredEventSourcingHandler[]
    //tag::BikeReturnedEventSourcingHandler[]
    @EventSourcingHandler
    protected void handle(BikeReturnedEvent event) {
        this.isAvailable = true;
        this.reservationConfirmed = false;
        this.reservedBy = null;
    }

    @EventSourcingHandler
    public void on(GeneralRentalEvent event) {
        // Reconstruct state from events
        this.bikeId = event.getBikeId();
        this.isAvailable = event.getEventType().equals("BikeRegistration");
        this.reservedBy = event.getRenter();
    }
    //end::BikeReturnedEventSourcingHandler[]
    //tag::BikeRequestedEventSourcingHandler[]
//    @EventSourcingHandler
//    protected void handle(BikeRequestedEvent event) {
//        this.reservedBy = event.getRenter();
//        this.reservationConfirmed = false;
//        this.isAvailable = false;
//    }

    //end::BikeRequestedEventSourcingHandler[]
    //tag::BikeRequestRejectedEventSourcingHandler[]
    @EventSourcingHandler
    protected void handle(RequestRejectedEvent event) {
        this.reservedBy = null;
        this.reservationConfirmed = false;
        this.isAvailable = true;
    }

    //end::BikeRequestRejectedEventSourcingHandler[]
    //tag::BikeInUseEventSourcingHandler[]
    @EventSourcingHandler
    protected void on(BikeInUseEvent event) {
        this.isAvailable = false;
        this.reservationConfirmed = true;
    }

    //end::BikeInUseEventSourcingHandler[]
    //tag::getters[]
    // getters for Jackson / JSON Serialization

    @SuppressWarnings("unused")
    public String getBikeId() {
        return bikeId;
    }

    @SuppressWarnings("unused")
    public boolean isAvailable() {
        return isAvailable;
    }

    @SuppressWarnings("unused")
    public String getReservedBy() {
        return reservedBy;
    }

    @SuppressWarnings("unused")
    public boolean isReservationConfirmed() {
        return reservationConfirmed;
    }
    //end::getters[]
}
//end::BikeAggregateClass[]
