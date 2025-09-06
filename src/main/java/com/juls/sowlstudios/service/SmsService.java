package com.juls.sowlstudios.service;

import com.juls.sowlstudios.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    @Value("${mnotify.api.key}")
    private String apiKey;

    @Value("${mnotify.api.url}")
    private String apiUrl;

    @Value("${mnotify.sender.id}")
    private String senderId;

    @Value("${mnotify.enabled:false}")
    private boolean smsEnabled;

    @Value("${app.admin.default.phone}")
    private String adminPhoneNumber;

    @Async
    public void sendBookingConfirmation(Booking booking) {
        if (!smsEnabled) {
            log.info("SMS service is disabled. Skipping SMS for booking ID: {}", booking.getId());
            return;
        }

        try {
            String customerMessage = buildCustomerBookingSmsMessage(booking);
            sendSms(booking.getPhoneNumber(), customerMessage);
            log.info("Booking confirmation SMS sent to customer for booking ID: {}", booking.getId());

            String adminMessage = buildAdminNotificationSmsMessage(booking);
            sendSms(adminPhoneNumber, adminMessage);
            log.info("New booking notification SMS sent to admin for booking ID: {}", booking.getId());

        } catch (Exception e) {
            log.error("Failed to send SMS for booking ID: {}", booking.getId(), e);
        }
    }

    private void sendSms(String phoneNumber, String message) {
        if (!smsEnabled) {
            log.info("SMS service is disabled. Skipping SMS for phone: {}", phoneNumber);
            return;
        }

        try {

            // Split the phoneNumber string into individual numbers if it contains commas
            String[] phoneNumbers = phoneNumber.split(",");

            // Format the phone numbers as a JSON array
            String phoneNumbersJson = Arrays.stream(phoneNumbers)
                    .map(num -> "\"" + num.trim() + "\"") // Add quotes around each number
                    .collect(Collectors.joining(", ", "[", "]")); // Join as a JSON array


            // Prepare the request body as a JSON string
            String requestBody = String.format(
                    "{\"recipient\":%s, \"sender\":\"%s\", \"message\":\"%s\", \"is_schedule\":\"false\", \"schedule_date\":\"\"}",
                    phoneNumbersJson, // Use the formatted JSON array
                    senderId,
                    message
            );

            log.info("Request Body: {}", requestBody);

            // Create the HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Build the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "?key=" + apiKey))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            // Send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Log the response
            if (response.statusCode() == HttpStatus.OK.value()) {
                log.info("SMS sent successfully to: {}", phoneNumber);
                log.debug("API Response: {}", response.body());
            } else {
                log.warn("SMS sending failed with status: {} for phone: {}", response.statusCode(), phoneNumber);
                log.warn("API Response: {}", response.body());
            }

        } catch (Exception e) {
            log.error("Error sending SMS to: {}", phoneNumber, e);
        }
    }

    private String buildCustomerBookingSmsMessage(Booking booking) {
        return String.format(
                "Hello %s! Your photography booking for %s has been received. " +
                        "We'll contact you soon to confirm details. Thank you!",
                booking.getFirstName(),
                booking.getGraduationDate()
        );
    }

    private String buildAdminNotificationSmsMessage(Booking booking) {
        return String.format(
                "New Booking Alert! Name: %s %s Phone: %s Package: %s Date: %s",
                booking.getFirstName(),
                booking.getLastName(),
                booking.getPhoneNumber(),
                booking.getPackagePreference(),
                booking.getGraduationDate()
        );
    }
}
