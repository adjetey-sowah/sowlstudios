package com.juls.sowlstudios.controller;

import com.juls.sowlstudios.dto.BookingDto;
import com.juls.sowlstudios.dto.response.ApiResponse;
import com.juls.sowlstudios.dto.response.BookingResponseDto;
import com.juls.sowlstudios.entity.Booking;
import com.juls.sowlstudios.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"${app.cors.allowed-origins}"})
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDto>> createBooking(@Valid @RequestBody BookingDto bookingDto) {
        log.info("Creating new booking for: {} {}", bookingDto.getFirstName(), bookingDto.getLastName());
        BookingResponseDto booking = bookingService.createBooking(bookingDto);
        return ResponseEntity.ok(ApiResponse.success("Booking created successfully", booking));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookingResponseDto>>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Booking.BookingStatus status) {
        Page<BookingResponseDto> bookings = bookingService.getAllBookings(page, size, status);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDto>> getBookingById(@PathVariable Long id) {
        BookingResponseDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BookingResponseDto>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam Booking.BookingStatus status) {
        log.info("Updating booking status to {} for booking ID: {}", status, id);
        BookingResponseDto booking = bookingService.updateBookingStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Booking status updated successfully", booking));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBooking(@PathVariable Long id) {
        log.info("Deleting booking with ID: {}", id);
        bookingService.deleteBooking(id);
        return ResponseEntity.ok(ApiResponse.success("Booking deleted successfully", null));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BookingResponseDto>>> searchBookings(
            @RequestParam(required = false) Booking.BookingStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<BookingResponseDto> bookings = bookingService.searchBookings(status, startDate, endDate, page, size);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }
}
