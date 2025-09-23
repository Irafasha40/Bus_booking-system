package rw.busbooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rw.busbooking.dtos.BookingRequestDTO;
import rw.busbooking.dtos.BookingResponseDTO;
import rw.busbooking.security.CustomUserDetails;
import rw.busbooking.service.BookingService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<BookingResponseDTO> createBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody BookingRequestDTO request
    ) {
        Long userId = ((CustomUserDetails) userDetails).getUserId();
        BookingResponseDTO response = bookingService.createBooking(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<BookingResponseDTO>> getUserBookings(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = ((CustomUserDetails) userDetails).getUserId();
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(Map.of("message", "Booking cancelled successfully"));
    }

    @GetMapping("/trip/{tripId}/booked-seats")
    public ResponseEntity<List<Integer>> getBookedSeatsByTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(bookingService.getBookedSeatNumbersForTrip(tripId));
    }
}

