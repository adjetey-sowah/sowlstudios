package com.juls.sowlstudios.service;

import com.juls.sowlstudios.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.admin}")
    private String adminEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    @Async
    public void sendBookingConfirmation(Booking booking) {
        if (!emailEnabled) {
            log.info("Email service is disabled. Skipping email for booking ID: {}", booking.getId());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("New Photography Booking - " + booking.getFirstName() + " " + booking.getLastName());

            String emailBody = buildBookingEmailBody(booking);
            message.setText(emailBody);

            mailSender.send(message);
            log.info("Booking confirmation email sent for booking ID: {}", booking.getId());

        } catch (Exception e) {
            log.error("Failed to send booking confirmation email for booking ID: {}", booking.getId(), e);
        }
    }

    private String buildBookingEmailBody(Booking booking) {
        return String.format("""
            New Photography Booking Received

            Booking Details:
            - Name: %s %s
            - Phone: %s
            - School/University: %s
            - Graduation Date: %s
            - Package Preference: %s
            - Preferred Location: %s
            - Additional Requests: %s
            - Booking Date: %s

            Please contact the client to confirm the booking details.

            Best regards,
            Photography Booking System
            """,
            booking.getFirstName(),
            booking.getLastName(),
            booking.getPhoneNumber(),
            booking.getSchoolUniversity(),
            booking.getGraduationDate(),
            booking.getPackagePreference(),
            booking.getPreferredLocation() != null ? booking.getPreferredLocation() : "Not specified",
            booking.getAdditionalRequests() != null ? booking.getAdditionalRequests() : "None",
            booking.getCreatedAt()
        );
    }
}
