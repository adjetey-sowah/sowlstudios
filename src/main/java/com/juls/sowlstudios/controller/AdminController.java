package com.juls.sowlstudios.controller;

import com.juls.sowlstudios.dto.BookingStatsDto;
import com.juls.sowlstudios.dto.response.ApiResponse;
import com.juls.sowlstudios.dto.response.BookingResponseDto;
import com.juls.sowlstudios.entity.Booking;
import com.juls.sowlstudios.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"${app.cors.allowed-origins}"})
public class AdminController {

    private final BookingService bookingService;

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<Page<BookingResponseDto>>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Booking.BookingStatus status) {
        Page<BookingResponseDto> bookings = bookingService.getAllBookings(page, size, status);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDto>> getBookingById(@PathVariable Long id) {
        BookingResponseDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<ApiResponse<BookingResponseDto>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam Booking.BookingStatus status) {
        log.info("Admin updating booking status to {} for booking ID: {}", status, id);
        BookingResponseDto booking = bookingService.updateBookingStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Booking status updated successfully", booking));
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBooking(@PathVariable Long id) {
        log.info("Admin deleting booking with ID: {}", id);
        bookingService.deleteBooking(id);
        return ResponseEntity.ok(ApiResponse.success("Booking deleted successfully", null));
    }

    @GetMapping("/bookings/search")
    public ResponseEntity<ApiResponse<Page<BookingResponseDto>>> searchBookings(
            @RequestParam(required = false) Booking.BookingStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<BookingResponseDto> bookings = bookingService.searchBookings(status, startDate, endDate, page, size);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<BookingStatsDto>> getBookingStatistics() {
        BookingStatsDto stats = bookingService.getBookingStatistics();
        return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", stats));
    }
}
