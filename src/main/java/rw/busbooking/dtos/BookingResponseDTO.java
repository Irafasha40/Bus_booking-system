package rw.busbooking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Long bookingId;
    private Long tripId;
    private Long userId;
    private List<Integer> seatNumbers;
    private Integer totalPrice;
    private String bookingStatus;
    private LocalDateTime bookingDate;
    private TripSummaryDTO tripDetails;
    private List<PassengerDTO> passengerDetails;

}

