package com.example.service_marketplace.Service;

import com.example.service_marketplace.Dto.BookingDto;

import java.util.List;
import java.util.Map;

public interface BookingService {
    BookingDto createBooking(BookingDto dto);
    List<BookingDto> getBookingsByUser(Long userId);
    List<BookingDto> getAllBookings();
    List<BookingDto> getBookingsByProvider(Long providerId);
    BookingDto updateStatus(Long bookingId, String status);

    double getTotalEarningsForProvider(Long providerId);
    double getTotalEarnings();
    double getCurrentMonthEarnings();

    // NEW: Revenue per month for the past 12 months
    List<Map<String, Object>> getMonthlyEarningsForPastYear();

    void deleteBooking(Long bookingId);
}
