package com.juls.sowlstudios.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "School/University is required")
    private String schoolUniversity;

    @NotNull(message = "Graduation date is required")
    @Future(message = "Graduation date must be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate graduationDate;

    @NotBlank(message = "Package preference is required")
    private String packagePreference;

    @Size(max = 255, message = "Preferred location cannot exceed 255 characters")
    private String preferredLocation;

    @Size(max = 1000, message = "Additional requests cannot exceed 1000 characters")
    private String additionalRequests;
}
