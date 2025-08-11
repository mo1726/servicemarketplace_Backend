package com.example.service_marketplace.Repository;

import com.example.service_marketplace.Entity.Booking;
import com.example.service_marketplace.Entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    // Get all bookings made by a user
    List<Booking> findByUserId(Long userId);

    // Get all bookings for services owned by a provider
    List<Booking> findByServiceProviderId(Long providerId);
    List<Booking> findByService_Provider_IdAndStatus(Long providerId, BookingStatus status);
    // All completed bookings, no date filter
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByStatusAndDateBetween(BookingStatus status, LocalDate start, LocalDate end);

}
