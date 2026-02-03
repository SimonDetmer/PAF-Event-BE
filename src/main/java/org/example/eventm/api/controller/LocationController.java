package org.example.eventm.api.controller;

import jakarta.validation.Valid;
import org.example.eventm.api.model.Location;
import org.example.eventm.api.repository.LocationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationRepository locationRepository;

    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // ------------------------------------------------------------
    // LOAD ALL LOCATIONS
    // ------------------------------------------------------------
    @GetMapping
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    // ------------------------------------------------------------
    // ADD LOCATION
    // ------------------------------------------------------------
    @PostMapping
    public Location createLocation(@Valid @RequestBody Location location) {
        // Name, city, capacity werden direkt aus dem Request-Body übernommen
        return locationRepository.save(location);
    }

    // ------------------------------------------------------------
    // LOAD LOCATION WITH ID
    // ------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Integer id) {
        return locationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------------------------------------------------
    // REFRESH LOCATIONS
    // ------------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(@PathVariable Integer id,
                                                   @Valid @RequestBody Location updatedLocation) {
        return locationRepository.findById(id)
                .map(existingLocation -> {
                    // Felder des neuen Location-Modells übernehmen
                    existingLocation.setName(updatedLocation.getName());
                    existingLocation.setCity(updatedLocation.getCity());
                    existingLocation.setCapacity(updatedLocation.getCapacity());

                    Location savedLocation = locationRepository.save(existingLocation);
                    return ResponseEntity.ok(savedLocation);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------------------------------------------------
    // DELETE LOCATION
    // ------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Integer id) {
        if (locationRepository.existsById(id)) {
            locationRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
