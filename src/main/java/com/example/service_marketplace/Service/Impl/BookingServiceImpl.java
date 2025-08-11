package com.example.service_marketplace.Service.Impl;

import com.example.service_marketplace.Dto.BookingDto;
import com.example.service_marketplace.Entity.Booking;
import com.example.service_marketplace.Entity.BookingStatus;
import com.example.service_marketplace.Entity.Service;
import com.example.service_marketplace.Entity.User;
import com.example.service_marketplace.Mapper.BookingMapper;
import com.example.service_marketplace.Repository.BookingRepository;
import com.example.service_marketplace.Repository.ServiceRepository;
import com.example.service_marketplace.Repository.UserRepository;
import com.example.service_marketplace.Service.BookingService;
import com.example.service_marketplace.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    @Override
    @Transactional
    public BookingDto createBooking(BookingDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Service service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        Booking booking = bookingMapper.toEntity(dto);

        // If the client didn't send a date, default to today.
        LocalDate date = booking.getDate() != null ? booking.getDate() : LocalDate.now();
        booking.setDate(date);

        booking.setUser(user);
        booking.setService(service);
        booking.setStatus(BookingStatus.PENDING);

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByProvider(Long providerId) {
        return bookingRepository.findByServiceProviderId(providerId)
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDto updateStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        BookingStatus to;
        try {
            to = BookingStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid booking status: " + status);
        }

        BookingStatus from = booking.getStatus();
        if (!isAllowedTransition(from, to)) {
            throw new IllegalStateException("Illegal status transition: " + from + " → " + to);
        }

        booking.setStatus(to);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public double getTotalEarnings() {
        List<Booking> completedBookings = bookingRepository.findByStatus(BookingStatus.COMPLETED);
        return completedBookings.stream()
                .mapToDouble(b -> b.getService().getPrice())
                .sum();
    }
    @Override
    @Transactional(readOnly = true)
    public double getCurrentMonthEarnings() {
        LocalDate now = LocalDate.now();
        return bookingRepository.findByStatusAndDateBetween(
                        BookingStatus.COMPLETED,
                        now.withDayOfMonth(1),
                        now.withDayOfMonth(now.lengthOfMonth())
                ).stream()
                .mapToDouble(b -> b.getService().getPrice())
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMonthlyEarningsForPastYear() {
        LocalDate now = LocalDate.now();
        List<Map<String, Object>> results = new ArrayList<>();

        for (int i = 11; i >= 0; i--) {
            LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
            double sum = bookingRepository.findByStatusAndDateBetween(
                            BookingStatus.COMPLETED,
                            monthStart,
                            monthEnd
                    ).stream()
                    .mapToDouble(b -> b.getService().getPrice())
                    .sum();
            results.add(Map.of(
                    "month", monthStart.getMonth().toString().substring(0, 3),
                    "year", monthStart.getYear(),
                    "total", sum
            ));
        }
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public double getTotalEarningsForProvider(Long providerId) {
        List<Booking> completedBookings =
                bookingRepository.findByService_Provider_IdAndStatus(providerId, BookingStatus.COMPLETED);
        return completedBookings.stream()
                .mapToDouble(b -> b.getService().getPrice())
                .sum();
    }

    @Override
    @Transactional
    public void deleteBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new ResourceNotFoundException("Booking not found");
        }
        bookingRepository.deleteById(bookingId);
    }

    // ---------- helpers ----------

    /**
     * Allowed transitions:
     * PENDING -> ACCEPTED, CANCELED
     * ACCEPTED -> IN_PROGRESS, CANCELED
     * IN_PROGRESS -> COMPLETED, CANCELED
     * COMPLETED -> (terminal)
     * CANCELED -> (terminal)
     */
    private boolean isAllowedTransition(BookingStatus from, BookingStatus to) {
        if (from == to) return true;

        Set<BookingStatus> next;
        switch (from) {
            case PENDING -> next = EnumSet.of(BookingStatus.ACCEPTED, BookingStatus.CANCELED);
            case ACCEPTED -> next = EnumSet.of(BookingStatus.IN_PROGRESS, BookingStatus.CANCELED);
            case IN_PROGRESS -> next = EnumSet.of(BookingStatus.COMPLETED, BookingStatus.CANCELED);
            case COMPLETED, CANCELED -> next = EnumSet.noneOf(BookingStatus.class);
            default -> next = EnumSet.noneOf(BookingStatus.class);
        }
        return next.contains(to);
    }
}
