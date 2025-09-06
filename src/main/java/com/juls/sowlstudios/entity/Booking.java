package com.juls.sowlstudios.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_booking_created_at", columnList = "created_at"),
        @Index(name = "idx_booking_status", columnList = "status"),
        @Index(name = "idx_booking_graduation_date", columnList = "graduation_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @NotBlank(message = "School/University is required")
    @Column(name = "school_university", nullable = false, length = 100)
    private String schoolUniversity;

    @NotNull(message = "Graduation date is required")
    @Future(message = "Graduation date must be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "graduation_date", nullable = false)
    private LocalDate graduationDate;

    @NotBlank(message = "Package preference is required")
    @Column(name = "package_preference", nullable = false, length = 50)
    private String packagePreference;

    @Size(max = 255, message = "Preferred location cannot exceed 255 characters")
    @Column(name = "preferred_location", length = 255)
    private String preferredLocation;

    @Size(max = 1000, message = "Additional requests cannot exceed 1000 characters")
    @Column(name = "additional_requests", columnDefinition = "TEXT")
    private String additionalRequests;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "email_sent", nullable = false)
    private Boolean emailSent = false;

    @Builder.Default
    @Column(name = "sms_sent", nullable = false)
    private Boolean smsSent = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }
}
