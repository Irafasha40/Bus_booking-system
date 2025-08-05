package rw.busbooking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripSummaryDTO {
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private String busNumber;

}

