package io.axoniq.demo.bikerental.rental.command.controller;

import io.axoniq.demo.bikerental.coreapi.rental.RegisterBikeCommand;
import io.axoniq.demo.bikerental.rental.command.entity.BikeCollection;
import io.axoniq.demo.bikerental.rental.command.repository.BikeCollectionRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class RentalController {


    private static final Logger log = LoggerFactory.getLogger(RentalController.class);
    private final CommandGateway commandGateway;

    private final BikeRentalDataGenerator bikeRentalDataGenerator;
    private final BikeCollectionRepository bikeCollectionRepository;

    public RentalController(CommandGateway commandGateway, BikeRentalDataGenerator bikeRentalDataGenerator, BikeCollectionRepository bikeCollectionRepository) { // <.>
        this.commandGateway = commandGateway;
        this.bikeRentalDataGenerator = bikeRentalDataGenerator;
        this.bikeCollectionRepository = bikeCollectionRepository;
    }

    @PostMapping("/bikes") // <.>
    public CompletableFuture<String> registerBike(
            @RequestParam("bikeType") String bikeType,
            @RequestParam("location") String location) {

        String bikeId = UUID.randomUUID().toString();
        RegisterBikeCommand registerBikeCommand =
                new RegisterBikeCommand(
                        bikeId,
                        bikeType,
                        location);

        CompletableFuture<String> commandResult =
                commandGateway.send(registerBikeCommand);

        BikeCollection bikeCollection = new BikeCollection(bikeId, bikeType, location);
        log.info("Bike Save in write db " + bikeCollection.getBikeId());
        bikeCollectionRepository.save(bikeCollection);

        return commandResult;
    }

    @PostMapping("/bikes/batch") // <.>
    public CompletableFuture<Void> generateBikes(@RequestParam("count") int bikeCount,              //<.>
                                                 @RequestParam(value = "type") String bikeType) {   //<.>
        CompletableFuture<Void> all = CompletableFuture.completedFuture(null);
        for (int i = 0; i < bikeCount; i++) {
            all = CompletableFuture.allOf(all,
                    commandGateway.send(new RegisterBikeCommand(UUID.randomUUID().toString(), bikeType, this.bikeRentalDataGenerator.randomLocation())));
        }
        return all;
    }


    @PostMapping(value = "/generateBikeRentals")
    public  CompletableFuture<String> generateRentalData(@RequestParam(value = "bikeId") String bikeId) {
         return this.bikeRentalDataGenerator.generateBikeRentals(bikeId);
    }


}
