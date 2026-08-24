package com.duft.salary_management.Utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import com.duft.salary_management.Enums.Currency;

public final class CurrencyConverter {

    private CurrencyConverter() {
        // Utility class
    }

    // Approximate static rates to USD (good enough for prototype)
    private static final Map<Currency, BigDecimal> TO_USD_RATES = Map.of(
            Currency.USD, BigDecimal.ONE,
            Currency.INR, new BigDecimal("0.012"),
            Currency.EUR, new BigDecimal("1.08"),
            Currency.GBP, new BigDecimal("1.27"),
            Currency.SGD, new BigDecimal("0.74"),
            Currency.AUD, new BigDecimal("0.65"),
            Currency.CAD, new BigDecimal("0.73"),
            Currency.AED, new BigDecimal("0.27"),
            Currency.JPY, new BigDecimal("0.0067")
    );

    public static BigDecimal toUsd(BigDecimal amount, Currency currency) {
        if (amount == null || currency == null) {
            throw new IllegalArgumentException("Amount and currency must not be null");
        }

        BigDecimal rate = TO_USD_RATES.get(currency);
        if (rate == null) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }

        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
