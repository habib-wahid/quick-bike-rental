package io.axoniq.demo.bikerental.rental.command.repository;

import io.axoniq.demo.bikerental.rental.command.entity.BikeCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository //<.>
public interface BikeCollectionRepository
        extends MongoRepository<BikeCollection, String> { //<.>

//    //tag::QueryMethods[]
//    List<BikeStatus> findAllByBikeTypeAndStatus(String bikeType, RentalStatus status);
//    long countBikeStatusesByBikeType(String bikeType);
//    //end::QueryMethods[]
//    List<BikeStatus> findByBikeId(String bikeId);

}