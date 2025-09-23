package rw.busbooking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trips")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripId;

    private String origin;

    private String destination;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    private Integer price;

    private Integer totalSeats;

    private Integer availableSeats;

    private String busNumber;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();


}

