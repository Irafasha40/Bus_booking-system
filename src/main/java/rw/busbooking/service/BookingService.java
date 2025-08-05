package rw.busbooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import rw.busbooking.dtos.BookingRequestDTO;
import rw.busbooking.dtos.BookingResponseDTO;
import rw.busbooking.dtos.PassengerDTO;
import rw.busbooking.dtos.TripSummaryDTO;
import rw.busbooking.model.*;
import rw.busbooking.repository.BookingRepository;
import rw.busbooking.repository.PassengerRepository;
import rw.busbooking.repository.TripRepository;
import rw.busbooking.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;

    public BookingResponseDTO createBooking(Long userId, BookingRequestDTO dto) {
        TripService trip = tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new NoSuchElementException("TripService not found"));

        for (Integer seat : dto.getSeatNumbers()) {
            if (bookingRepository.existsByTrip_TripIdAndSeatNumbersContaining(trip.getTripId(), seat)) {
                throw new IllegalStateException("Seat " + seat + " already booked");
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTrip(trip);
        booking.setSeatNumbers(dto.getSeatNumbers());
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setBookingDate(LocalDateTime.now());

        List<Passenger> passengers = dto.getPassengerDetails().stream().map(pd -> {
            Passenger p = new Passenger();
            BeanUtils.copyProperties(pd, p);
            p.setBooking(booking);
            return p;
        }).collect(Collectors.toList());

        booking.setPassengerDetails(passengers);

        bookingRepository.save(booking);
        passengerRepository.saveAll(passengers);

        trip.setAvailableSeats(trip.getAvailableSeats() - dto.getSeatNumbers().size());
        tripRepository.save(trip);

        return convertToResponse(booking);
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking not found"));

        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.getTrip().setAvailableSeats(booking.getTrip().getAvailableSeats() + booking.getSeatNumbers().size());

        bookingRepository.save(booking);
        tripRepository.save(booking.getTrip());
    }

    public List<BookingResponseDTO> getUserBookings(Long userId) {
        return bookingRepository.findByUser_UserId(userId)
                .stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private BookingResponseDTO convertToResponse(Booking b) {
        TripService trip = b.getTrip();
        TripSummaryDTO summary = new TripSummaryDTO(
                trip.getOrigin(), trip.getDestination(), trip.getDepartureTime(), trip.getBusNumber());

        List<PassengerDTO> passengers = b.getPassengerDetails().stream().map(p ->
                new PassengerDTO(p.getName(), p.getAge(), p.getGender(), p.getSeatNumber())
        ).collect(Collectors.toList());

        return new BookingResponseDTO(
                b.getBookingId(), trip.getTripId(), b.getUser().getUserId(),
                b.getSeatNumbers(), trip.getPrice(), b.getBookingStatus().name(),
                b.getBookingDate(), summary, passengers
        );
    }
}

