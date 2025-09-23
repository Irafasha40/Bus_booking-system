package rw.busbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.busbooking.model.Trip;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByOriginIgnoreCase(String origin);

    List<Trip> findByDestinationIgnoreCase(String destination);

    List<Trip> findByDepartureTimeBetween(LocalDateTime from, LocalDateTime to);
}

