package com.foodya.foodya_backend.utils.phone;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility class for phone number validation and normalization
 * Supports automatic formatting and country code addition
 */
public class PhoneNumberUtil {

    // Country codes mapping
    private static final Map<String, String> COUNTRY_CODES = Map.of(
            "VN", "+84",   // Vietnam
            "US", "+1",    // United States
            "TW", "+886",  // Taiwan
            "CN", "+86",   // China
            "JP", "+81",   // Japan
            "KR", "+82",   // South Korea
            "TH", "+66",   // Thailand
            "SG", "+65"    // Singapore
    );

    // Default country for the application
    private static final String DEFAULT_COUNTRY = "VN";

    // Pattern for valid phone number (digits only, 7-15 length)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{7,15}$");

    // Pattern for international format
    private static final Pattern INTERNATIONAL_PATTERN = Pattern.compile("^\\+\\d{1,4}\\d{7,15}$");

    /**
     * Normalize phone number to international format (+CountryCodeNumber)
     *
     * Examples:
     * - "0987654321" (VN) -> "+84987654321"
     * - "987654321" (VN) -> "+84987654321"
     * - "+84987654321" -> "+84987654321" (already normalized)
     * - "098-765-4321" (VN) -> "+84987654321"
     * - "098 765 4321" (VN) -> "+84987654321"
     *
     * @param input Raw phone number input
     * @param countryCode Country code (e.g., "VN", "US") - optional, defaults to VN
     * @return Normalized phone number in international format, or null if input is null
     * @throws IllegalArgumentException if phone number format is invalid
     */
    public static String normalize(String input, String countryCode) {
        if (input == null || input.isBlank()) {
            return null;
        }

        // Remove all non-digit characters except +
        String cleaned = input.replaceAll("[^\\d+]", "");

        // If already in international format, validate and return
        if (cleaned.startsWith("+")) {
            if (!INTERNATIONAL_PATTERN.matcher(cleaned).matches()) {
                throw new IllegalArgumentException(
                        "Invalid international phone number format: " + input);
            }
            return cleaned;
        }

        // Remove leading zero if present (local format)
        if (cleaned.startsWith("0")) {
            cleaned = cleaned.substring(1);
        }

        // Validate cleaned number
        if (!PHONE_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException(
                    "Invalid phone number format: " + input + ". Must be 7-15 digits.");
        }

        // Get country code prefix
        String country = (countryCode != null && !countryCode.isBlank())
                ? countryCode.toUpperCase()
                : DEFAULT_COUNTRY;

        String prefix = COUNTRY_CODES.getOrDefault(country, COUNTRY_CODES.get(DEFAULT_COUNTRY));

        return prefix + cleaned;
    }

    /**
     * Normalize phone number using default country (VN)
     *
     * @param input Raw phone number input
     * @return Normalized phone number in international format
     */
    public static String normalize(String input) {
        return normalize(input, DEFAULT_COUNTRY);
    }

    /**
     * Validate if phone number is in correct international format
     *
     * @param phoneNumber Phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return false;
        }
        return INTERNATIONAL_PATTERN.matcher(phoneNumber).matches();
    }

    /**
     * Format phone number for display
     * Example: +84987654321 -> +84 98 765 4321
     *
     * @param phoneNumber Phone number in international format
     * @return Formatted phone number for display
     */
    public static String format(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.startsWith("+")) {
            return phoneNumber;
        }

        // Extract country code and number
        String number = phoneNumber.substring(1);
        String countryCode = "";

        // Try to identify country code
        for (String code : COUNTRY_CODES.values()) {
            String codeDigits = code.substring(1);
            if (number.startsWith(codeDigits)) {
                countryCode = code;
                number = number.substring(codeDigits.length());
                break;
            }
        }

        // Format the number part (add spaces every 2-3 digits)
        if (number.length() >= 9) {
            // Format as: XX XXX XXXX or XXX XXX XXX
            if (number.length() == 9) {
                return String.format("%s %s %s %s",
                    countryCode,
                    number.substring(0, 2),
                    number.substring(2, 5),
                    number.substring(5));
            } else if (number.length() == 10) {
                return String.format("%s %s %s %s",
                    countryCode,
                    number.substring(0, 3),
                    number.substring(3, 6),
                    number.substring(6));
            }
        }

        return countryCode + " " + number;
    }

    /**
     * Extract country code from phone number
     *
     * @param phoneNumber Phone number in international format
     * @return Country code (e.g., "+84") or null if not found
     */
    public static String extractCountryCode(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.startsWith("+")) {
            return null;
        }

        for (String code : COUNTRY_CODES.values()) {
            if (phoneNumber.startsWith(code)) {
                return code;
            }
        }

        return null;
    }

    /**
     * Get all supported country codes
     *
     * @return Map of country code -> phone prefix
     */
    public static Map<String, String> getSupportedCountryCodes() {
        return Map.copyOf(COUNTRY_CODES);
    }
}
