package io.axoniq.demo.bikerental.rental.command.service;

import io.axoniq.demo.bikerental.rental.command.entity.BikeCollection;
import io.axoniq.demo.bikerental.rental.command.repository.BikeCollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BikesService {

    @Autowired
    private BikeCollectionRepository bikeCollectionRepository;

//    public BikesService(BikeRepository bikeRepository) {
//        this.bikeRepository = bikeRepository;
//    }

    public void save(BikeCollection bike) {
        bikeCollectionRepository.save(bike);
    }
}
