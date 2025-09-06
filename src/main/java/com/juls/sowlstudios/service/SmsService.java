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
import java.util.HashMap;
import java.util.Map;

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

    @Async
    public void sendBookingConfirmation(Booking booking) {
        if (!smsEnabled) {
            log.info("SMS service is disabled. Skipping SMS for booking ID: {}", booking.getId());
            return;
        }

        try {
            String message = buildBookingSmsMessage(booking);
            sendSms(booking.getPhoneNumber(), message);
            log.info("Booking confirmation SMS sent for booking ID: {}", booking.getId());

        } catch (Exception e) {
            log.error("Failed to send booking confirmation SMS for booking ID: {}", booking.getId(), e);
        }
    }

    private void sendSms(String phoneNumber, String message) {
        if (!smsEnabled) {
            log.info("SMS service is disabled. Skipping SMS for phone: {}", phoneNumber);
            return;
        }

        try {
            // Prepare the request body as a JSON string
            String requestBody = String.format(
                    "{\"recipient\":[\"%s\"], \"sender\":\"%s\", \"message\":\"%s\"}",
                    phoneNumber, senderId, message
            );

            // Create the HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Build the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "?key=" + apiKey)) // Append the API key to the URL
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody)) // Set the request body
                    .header("Content-Type", "application/json") // Set the content type
                    .build();

            // Send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Log the response
            if (response.statusCode() == HttpStatus.OK.value()) {
                log.info("SMS sent successfully to: {}", phoneNumber);
            } else {
                log.warn("SMS sending failed with status: {} for phone: {}", response.statusCode(), phoneNumber);
            }

        } catch (Exception e) {
            log.error("Error sending SMS to: {}", phoneNumber, e);
        }
    }


    private String buildBookingSmsMessage(Booking booking) {
        return String.format(
            "Hello %s! Your photography booking for %s has been received. " +
            "We'll contact you soon to confirm details. Thank you!",
            booking.getFirstName(),
            booking.getGraduationDate()
        );
    }
}
