package io.axoniq.demo.bikerental.rental.query;

import io.axoniq.demo.bikerental.coreapi.rental.BikeStatus;
import io.axoniq.demo.bikerental.coreapi.rental.BikeStatusNamedQueries;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class RentalController {

    private final CommandGateway commandGateway;    // <.>
    private final QueryGateway queryGateway;
    private final BikeStatusRepository bikeStatusRepository;
    // <.>
    //end::BusGateways[]


    public RentalController(CommandGateway commandGateway, QueryGateway queryGateway, BikeStatusRepository bikeStatusRepository) { // <.>
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.bikeStatusRepository = bikeStatusRepository;
    }

    @GetMapping("/bikes") //<.>
    public CompletableFuture<List<BikeStatus>> findAll() { //<.>
        return queryGateway.query( //<.>
                BikeStatusNamedQueries.FIND_ALL, //<.>
                null, //<.>
                ResponseTypes.multipleInstancesOf(BikeStatus.class) //<.>
        );
    }

    @GetMapping("/bikes/{bikeId}") // <.>
    public CompletableFuture<BikeStatus> findStatus(@PathVariable("bikeId") String bikeId) {
        System.out.println("Bike Id : " + bikeId);
        return queryGateway.query(BikeStatusNamedQueries.FIND_ONE, bikeId, BikeStatus.class); //<.>
    }

    @GetMapping("/getBike")
    public void getBike(@RequestParam(value = "bikeId") String bikeId) {
        BikeStatus bikeStatus = bikeStatusRepository.findByBikeId(bikeId);
        System.out.println("Bike Id : " + bikeStatus.getBikeId());
    }
}
