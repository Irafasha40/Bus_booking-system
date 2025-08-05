package rw.busbooking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    private Long tripId;
    private List<Integer> seatNumbers;
    private List<PassengerDTO> passengerDetails;
}
