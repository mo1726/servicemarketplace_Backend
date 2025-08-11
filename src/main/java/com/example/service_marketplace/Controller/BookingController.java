package com.example.service_marketplace.Controller;

import com.example.service_marketplace.Dto.BookingDto;
import com.example.service_marketplace.Service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> create(@Valid @RequestBody BookingDto dto,
                                             UriComponentsBuilder uriBuilder) {
        BookingDto saved = bookingService.createBooking(dto);
        URI location = uriBuilder.path("/bookings/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved); // 201 Created
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDto>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<BookingDto>> getByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(bookingService.getBookingsByProvider(providerId));
    }

    @GetMapping("/provider/{providerId}/earnings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getProviderEarnings(@PathVariable Long providerId) {
        return ResponseEntity.ok(bookingService.getTotalEarningsForProvider(providerId));
    }
    @GetMapping
    public List<BookingDto> getAllBookings() {
        return bookingService.getAllBookings();
    }
    @GetMapping("/earnings/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getTotalEarnings() {
        return ResponseEntity.ok(bookingService.getTotalEarnings());
    }
    @GetMapping("/earnings/monthly/current")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getCurrentMonthEarnings() {
        return ResponseEntity.ok(bookingService.getCurrentMonthEarnings());
    }

    @GetMapping("/earnings/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyEarnings() {
        return ResponseEntity.ok(bookingService.getMonthlyEarningsForPastYear());
    }

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<BookingDto> updateStatus(@PathVariable Long bookingId,
                                                   @RequestParam String status) {
        return ResponseEntity.ok(bookingService.updateStatus(bookingId, status));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build(); // 204
    }
}
