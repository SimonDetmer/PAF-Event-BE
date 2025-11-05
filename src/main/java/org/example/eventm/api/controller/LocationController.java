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

    @GetMapping
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @PostMapping
    public Location createLocation(@Valid @RequestBody Location location) {
        return locationRepository.save(location);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Integer id) {
        return locationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(@PathVariable Integer id, @Valid @RequestBody Location updatedLocation) {
        return locationRepository.findById(id)
                .map(existingLocation -> {
                    // Beispiel: Aktualisiere den Namen. Weitere Felder können hier ergänzt werden.
                    existingLocation.setStreet(updatedLocation.getStreet());
                    Location savedLocation = locationRepository.save(existingLocation);
                    return ResponseEntity.ok(savedLocation);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Integer id) {
        if (locationRepository.existsById(id)) {
            locationRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
