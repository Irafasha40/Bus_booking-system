package rw.busbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.busbooking.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser_UserId(Long userId);

    List<Booking> findByTrip_TripId(Long tripId);

    Boolean existsByTrip_TripIdAndSeatNumbersContaining(Long tripId, Integer seatNumber);
}

