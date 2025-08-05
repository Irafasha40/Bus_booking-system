package rw.busbooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.busbooking.dtos.TripRequestDTO;
import rw.busbooking.dtos.TripResponseDTO;
import rw.busbooking.service.TripService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TripResponseDTO> createTrip(@RequestBody TripRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.createTrip(request));
    }

    @DeleteMapping("/{tripId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cancelTrip(@PathVariable Long tripId) {
        tripService.cancelTrip(tripId);
        return ResponseEntity.ok(Map.of("message", "TripService cancelled successfully"));
    }

    @GetMapping
    public ResponseEntity<List<TripResponseDTO>> getAllTrips() {
        return ResponseEntity.ok(tripService.findAllTrips());
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponseDTO> getTripById(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.findTripById(tripId));
    }
}

