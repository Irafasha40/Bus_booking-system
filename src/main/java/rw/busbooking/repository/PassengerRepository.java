package rw.busbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.busbooking.model.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    // Optional: add passenger-based search logic if needed
}

