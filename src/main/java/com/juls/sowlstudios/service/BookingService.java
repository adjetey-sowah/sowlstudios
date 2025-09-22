package com.juls.sowlstudios.service;



import com.juls.sowlstudios.dto.BookingDto;
import com.juls.sowlstudios.dto.BookingStatsDto;
import com.juls.sowlstudios.dto.response.BookingResponseDto;
import com.juls.sowlstudios.entity.Booking;
import com.juls.sowlstudios.exception.BookingException;
import com.juls.sowlstudios.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    @Transactional
    @CacheEvict(value = {"bookings", "stats"}, allEntries = true)
    public BookingResponseDto createBooking(BookingDto bookingDto) {
        try {
            Booking booking = convertToEntity(bookingDto);
            Booking savedBooking = bookingRepository.save(booking);

            // Send notifications asynchronously
            emailService.sendBookingConfirmation(savedBooking);
            smsService.sendBookingConfirmation(savedBooking);

            log.info("Booking created successfully for: {} {} {} with ID: {}",
                    savedBooking.getFirstName(), savedBooking.getLastName(), savedBooking.getAmount(), savedBooking.getId());

            return BookingResponseDto.fromEntity(savedBooking);
        } catch (Exception e) {
            log.error("Error creating booking", e);
            throw new BookingException("Failed to create booking: " + e.getMessage());
        }
    }

    @Cacheable(value = "bookings", key = "#page + '-' + #size + '-' + #status")
    public Page<BookingResponseDto> getAllBookings(int page, int size, Booking.BookingStatus status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Booking> bookings;
        if (status != null) {
            bookings = bookingRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else {
            bookings = bookingRepository.findByOrderByCreatedAtDesc(pageable);
        }

        return bookings.map(BookingResponseDto::fromEntity);
    }

    public BookingResponseDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingException("Booking not found with id: " + id));
        return BookingResponseDto.fromEntity(booking);
    }

    @Transactional
    @CacheEvict(value = {"bookings", "stats"}, allEntries = true)
    public BookingResponseDto updateBookingStatus(Long id, Booking.BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingException("Booking not found with id: " + id));

        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking status updated to {} for booking ID: {}", status, id);
        return BookingResponseDto.fromEntity(updatedBooking);
    }

    @Transactional
    @CacheEvict(value = {"bookings", "stats"}, allEntries = true)
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new BookingException("Booking not found with id: " + id);
        }
        bookingRepository.deleteById(id);
        log.info("Booking deleted with ID: {}", id);
    }

    @Cacheable(value = "stats")
    public BookingStatsDto getBookingStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.minusWeeks(1);
        LocalDateTime monthStart = now.minusMonths(1);

        // Calculate start and end of today
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        Long totalBookings = bookingRepository.count();
        Long todayBookings = bookingRepository.countTodayBookings(startOfDay, endOfDay);
        Long weeklyBookings = bookingRepository.countWeeklyBookings(weekStart);
        Long monthlyBookings = bookingRepository.countMonthlyBookings(monthStart);
        Long pendingBookings = bookingRepository.countByStatus(Booking.BookingStatus.PENDING);
        Long confirmedBookings = bookingRepository.countByStatus(Booking.BookingStatus.CONFIRMED);
        Long cancelledBookings = bookingRepository.countByStatus(Booking.BookingStatus.CANCELLED);
        Long completedBookings = bookingRepository.countByStatus(Booking.BookingStatus.COMPLETED);

        List<Object[]> packageStatsRaw = bookingRepository.getPackageStatistics();
        Map<String, Long> packageStats = packageStatsRaw.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        return BookingStatsDto.builder()
                .totalBookings(totalBookings)
                .todayBookings(todayBookings)
                .weeklyBookings(weeklyBookings)
                .monthlyBookings(monthlyBookings)
                .pendingBookings(pendingBookings)
                .confirmedBookings(confirmedBookings)
                .cancelledBookings(cancelledBookings)
                .completedBookings(completedBookings)
                .packageStats(packageStats)
                .build();
    }

    public Page<BookingResponseDto> searchBookings(Booking.BookingStatus status,
                                                   LocalDateTime startDate,
                                                   LocalDateTime endDate,
                                                   int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Booking> bookings = bookingRepository.findBookingsWithFilters(status, startDate, endDate, pageable);
        return bookings.map(BookingResponseDto::fromEntity);
    }

    @Cacheable(value = "sales", key = "#startDate + '-' + #endDate + '-' + #status")
    public double calculateTotalSales(LocalDate startDate, LocalDate endDate, Booking.BookingStatus status) {
        List<Booking> bookings;

        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(23, 59, 59);

            if (status != null) {
                bookings = bookingRepository.findByCreatedAtBetweenAndStatus(start, end, status);
            } else {
                bookings = bookingRepository.findByCreatedAtBetween(start, end);
            }
        } else if (status != null) {
            bookings = bookingRepository.findByStatus(status);
        } else {
            bookings = bookingRepository.findAll();
        }

        return bookings.stream()
                .mapToDouble(booking -> booking.getAmount() != null ? booking.getAmount() : 0.0)
                .sum();
    }


    private Booking convertToEntity(BookingDto dto) {
        return Booking.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .schoolUniversity(dto.getSchoolUniversity())
                .graduationDate(dto.getGraduationDate())
                .packagePreference(dto.getPackagePreference())
                .preferredLocation(dto.getPreferredLocation())
                .additionalRequests(dto.getAdditionalRequests())
                .build();
    }
}