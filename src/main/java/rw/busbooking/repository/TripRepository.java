package rw.busbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.busbooking.model.TripService;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<TripService, Long> {

    List<TripService> findByOriginIgnoreCase(String origin);

    List<TripService> findByDestinationIgnoreCase(String destination);

    List<TripService> findByDepartureTimeBetween(LocalDateTime from, LocalDateTime to);
}

