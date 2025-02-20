package io.axoniq.demo.bikerental.rental.query;

import io.axoniq.demo.bikerental.coreapi.rental.*;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

//tag::ClassDefinition[]
@Component
public class BikeStatusProjection {

    //tag::Repository[]
    private final BikeStatusRepository bikeStatusRepository; //<.>
    //end::Repository[]
    //tag::UpdateEmitter[]
    private final QueryUpdateEmitter updateEmitter;

    //end::UpdateEmitter[]
    //tag::Constructor[]
    public BikeStatusProjection(BikeStatusRepository bikeStatusRepository, QueryUpdateEmitter updateEmitter) {
        this.bikeStatusRepository = bikeStatusRepository;
        this.updateEmitter = updateEmitter;
    }

    //end::Constructor[]
    //tag::EventHandlers[]
    //tag::BikeRegisteredEventHandler[]
    @EventHandler  //<.>
    public void on(BikeRegisteredEvent event) { //<.>
        System.out.println("Event here");
        var bikeStatus = new BikeStatus(event.getBikeId(), event.getBikeType(), event.getLocation()); //<.>
        bikeStatusRepository.save(bikeStatus); //<.>
        //tag::UpdateEmitter[]
        updateEmitter.emit(q -> "findAll".equals(q.getQueryName()), bikeStatus); //<.>
        //end::UpdateEmitter[]
    }

    //end::BikeRegisteredEventHandler[]
    @EventHandler
    public void on(BikeRequestedEvent event) {
        bikeStatusRepository.findById(event.getBikeId())
                            .map(bs -> {
                                bs.requestedBy(event.getRenter());
                                return bs;
                            })
                            .ifPresent(bs -> {
                                updateEmitter.emit(q -> "findAll".equals(q.getQueryName()), bs);
                                updateEmitter.emit(String.class, event.getBikeId()::equals, bs);
                            });
    }

    @EventHandler
    public void on(BikeInUseEvent event) {
        bikeStatusRepository.findById(event.bikeId())
                            .map(bs -> {
                                bs.rentedBy(event.renter());
                                return bs;
                            })
                            .ifPresent(bs -> {
                                updateEmitter.emit(q -> "findAll".equals(q.getQueryName()), bs);
                                updateEmitter.emit(String.class, event.bikeId()::equals, bs);
                            });
    }

    @EventHandler
    public void on(BikeReturnedEvent event) {
        bikeStatusRepository.findById(event.bikeId())
                            .map(bs -> {
                                bs.returnedAt(event.location());
                                return bs;
                            })
                            .ifPresent(bs -> {
                                updateEmitter.emit(q -> "findAll".equals(q.getQueryName()), bs);
                                updateEmitter.emit(String.class, event.bikeId()::equals, bs);
                            });
    }

    @EventHandler
    public void on(RequestRejectedEvent event) {
        bikeStatusRepository.findById(event.bikeId())
                            .map(bs -> {
                                bs.returnedAt(bs.getLocation());
                                return bs;
                            })
                            .ifPresent(bs -> {
                                updateEmitter.emit(q -> "findAll".equals(q.getQueryName()), bs);
                                updateEmitter.emit(String.class, event.bikeId()::equals, bs);
                            });
    }

    //end::EventHandlers[]
    //tag::QueryHandlers[]
    //tag::findAllQueryHandler[]
    @QueryHandler(queryName = BikeStatusNamedQueries.FIND_ALL) //<.>
    public Iterable<BikeStatus> findAll() { // <.>
        return bikeStatusRepository.findAll(); //<.>
    }

    //end::findAllQueryHandler[]
    //tag::findAvailableQueryHandler[]
    @QueryHandler(queryName = BikeStatusNamedQueries.FIND_AVAILABLE) //<.>
    public Iterable<BikeStatus> findAvailable(String bikeType) { //<.>
        return bikeStatusRepository.findAllByBikeTypeAndStatus(bikeType, RentalStatus.AVAILABLE);
    }

    //end::findAvailableQueryHandler[]
    //tag::findOneQueryHandler[]
    @QueryHandler(queryName = BikeStatusNamedQueries.FIND_ONE) // <.>
    public BikeStatus findOne(String bikeId) { //<.>
        return bikeStatusRepository.findById(bikeId).orElse(null); //<.>
    }

    //end::findOneQueryHandler[]
    @QueryHandler
    public long countOfBikesByType(CountOfBikesByTypeQuery query) {
        return bikeStatusRepository.countBikeStatusesByBikeType(query.bikeType());
    }
    //end::QueryHandlers[]
}
//end::ClassDefinition[]