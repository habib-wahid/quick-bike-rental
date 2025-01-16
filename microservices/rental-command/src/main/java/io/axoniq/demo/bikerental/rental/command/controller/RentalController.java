package io.axoniq.demo.bikerental.rental.command.controller;

import io.axoniq.demo.bikerental.coreapi.rental.RegisterBikeCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class RentalController {


    private final CommandGateway commandGateway;    // <.>
    private final QueryGateway queryGateway;        // <.>
    //end::BusGateways[]

    private final BikeRentalDataGenerator bikeRentalDataGenerator;

    public RentalController(CommandGateway commandGateway, QueryGateway queryGateway, BikeRentalDataGenerator bikeRentalDataGenerator) { // <.>
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.bikeRentalDataGenerator = bikeRentalDataGenerator;
    }

    @PostMapping("/bikes") // <.>
    public CompletableFuture<String> registerBike(
            @RequestParam("bikeType") String bikeType,      // <.>
            @RequestParam("location") String location) {    // <.>

        RegisterBikeCommand registerBikeCommand =
                new RegisterBikeCommand(                // <.>
                        UUID.randomUUID().toString(),   // <.>
                        bikeType,
                        location);

        CompletableFuture<String> commandResult =
                commandGateway.send(registerBikeCommand); //<.>

        return commandResult; // <.>
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

    @PostMapping(value = "/generateRentals")
    public Flux<String> generateData(@RequestParam(value = "bikeType") String bikeType,
                                     @RequestParam("loops") int loops,
                                     @RequestParam(value = "concurrency", defaultValue = "1") int concurrency,
                                     @RequestParam(value = "abandonPaymentFactor", defaultValue = "100") int abandonPaymentFactor,
                                     @RequestParam(value = "delay", defaultValue = "0")int delay) {

        return this.bikeRentalDataGenerator.generateRentals(bikeType, loops, concurrency, abandonPaymentFactor, delay);
    }

}
