package rw.busbooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import rw.busbooking.dtos.TripRequestDTO;
import rw.busbooking.dtos.TripResponseDTO;
import rw.busbooking.model.TripService;
import rw.busbooking.repository.TripRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;

    public TripResponseDTO createTrip(TripRequestDTO dto) {
        TripService trip = new TripService();
        BeanUtils.copyProperties(dto, trip);
        trip.setAvailableSeats(dto.getTotalSeats());

        tripRepository.save(trip);

        return convertToResponse(trip);
    }

    public void cancelTrip(Long tripId) {
        TripService trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NoSuchElementException("TripService not found"));
        tripRepository.delete(trip);
    }

    public List<TripResponseDTO> findAllTrips() {
        return tripRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public TripResponseDTO findTripById(Long tripId) {
        TripService trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NoSuchElementException("TripService not found"));
        return convertToResponse(trip);
    }

    private TripResponseDTO convertToResponse(TripService trip) {
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

