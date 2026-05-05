package com.lalin.ledger.account;

import java.util.Objects;


/**
 * Assuming domestic transfers:
 * Account number length = 8 digits. No letters and no whitespaces
 * @param accountNumberValue
 */
public record AccountNumber(String accountNumberValue) {

    private static final String VALIDATION_REGEX = "^\\d{8}$";

    public AccountNumber {
        // Null check
        Objects.requireNonNull(accountNumberValue, "Account number cannot be null");

        // Strip whitespace and hyphens for consistency
        accountNumberValue = accountNumberValue.replaceAll("\\s+|-", "");

        // Format validation
        if (!accountNumberValue.matches(VALIDATION_REGEX)) {
            throw new IllegalArgumentException("Invalid bank account number format");
        }
    }
}
