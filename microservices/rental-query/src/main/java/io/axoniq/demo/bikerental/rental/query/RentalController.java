package io.axoniq.demo.bikerental.rental.query;

import io.axoniq.demo.bikerental.coreapi.rental.BikeStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class RentalController {

    private final BikeStatusRepository bikeStatusRepository;

    public RentalController(BikeStatusRepository bikeStatusRepository) { // <.>
        this.bikeStatusRepository = bikeStatusRepository;
    }


    @GetMapping("/getBike")
    public BikeStatus getBike(@RequestParam(value = "bikeId") String bikeId) {
        return bikeStatusRepository.findById(bikeId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid bikeId: " + bikeId));
    }
}
