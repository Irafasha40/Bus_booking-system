package rw.busbooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import rw.busbooking.dtos.TripRequestDTO;
import rw.busbooking.dtos.TripResponseDTO;
import rw.busbooking.model.Trip;
import rw.busbooking.repository.TripRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;

    public TripResponseDTO createTrip(TripRequestDTO dto) {
        Trip trip = new Trip();
        BeanUtils.copyProperties(dto, trip);
        trip.setAvailableSeats(dto.getTotalSeats());

        tripRepository.save(trip);

        return convertToResponse(trip);
    }

    public void cancelTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NoSuchElementException("TripService not found"));
        tripRepository.delete(trip);
    }

    public List<TripResponseDTO> findAllTrips() {
        return tripRepository.findAll().stream()
                .filter(trip -> trip.getDepartureTime().isAfter(LocalDateTime.now()))
                .map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<TripResponseDTO> findAllTripsIncludingDeparted() {
        return tripRepository.findAll().stream()
                .map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<TripResponseDTO> findDepartedTrips() {
        return tripRepository.findAll().stream()
                .filter(trip -> trip.getDepartureTime().isBefore(LocalDateTime.now()))
                .map(this::convertToResponse).collect(Collectors.toList());
    }

    public TripResponseDTO findTripById(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NoSuchElementException("TripService not found"));
        
        return convertToResponse(trip);
    }

    public TripResponseDTO findTripByIdForAdmin(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NoSuchElementException("TripService not found"));
        
        return convertToResponse(trip);
    }

    private TripResponseDTO convertToResponse(Trip trip) {
        List<Integer> bookedSeats = trip.getBookings().stream()
                .flatMap(b -> b.getSeatNumbers().stream())
                .collect(Collectors.toList());

        return new TripResponseDTO(
                trip.getTripId(), trip.getOrigin(), trip.getDestination(),
                trip.getDepartureTime(), trip.getArrivalTime(), trip.getPrice(),
                trip.getTotalSeats(), trip.getAvailableSeats(),
                trip.getBusNumber(), bookedSeats
        );
    }
}

