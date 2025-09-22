package com.juls.sowlstudios.dto.response;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.juls.sowlstudios.entity.Booking;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String schoolUniversity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate graduationDate;

    private String packagePreference;
    private String preferredLocation;
    private String additionalRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private Boolean emailSent;
    private Boolean smsSent;
    private Booking.BookingStatus status;
    private Double amount;

    public static BookingResponseDto fromEntity(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .firstName(booking.getFirstName())
                .lastName(booking.getLastName())
                .phoneNumber(booking.getPhoneNumber())
                .schoolUniversity(booking.getSchoolUniversity())
                .graduationDate(booking.getGraduationDate())
                .packagePreference(booking.getPackagePreference())
                .preferredLocation(booking.getPreferredLocation())
                .additionalRequests(booking.getAdditionalRequests())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .emailSent(booking.getEmailSent())
                .smsSent(booking.getSmsSent())
                .status(booking.getStatus())
                .amount(booking.getAmount())
                .build();
    }
}