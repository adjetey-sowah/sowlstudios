package com.juls.sowlstudios.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class PhoneValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");

    public boolean isValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        String cleanedPhone = phoneNumber.replaceAll("\\s+", "");
        boolean isValid = PHONE_PATTERN.matcher(cleanedPhone).matches();

        if (!isValid) {
            log.warn("Invalid phone number format: {}", phoneNumber);
        }

        return isValid;
    }

    public String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }

        // Remove all spaces and non-digit characters except +
        String cleaned = phoneNumber.replaceAll("[^+0-9]", "");

        // Add + if it starts with country code but no +
        if (cleaned.length() > 10 && !cleaned.startsWith("+")) {
            cleaned = "+" + cleaned;
        }

        return cleaned;
    }

    public boolean isGhanaianNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }

        String cleaned = phoneNumber.replaceAll("[^0-9]", "");

        // Check for Ghanaian number patterns
        return cleaned.startsWith("233") ||
               (cleaned.length() == 10 && cleaned.startsWith("0"));
    }
}
