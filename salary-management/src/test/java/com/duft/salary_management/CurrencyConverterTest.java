package com.duft.salary_management;

import com.duft.salary_management.Enums.Currency;
import com.duft.salary_management.Utility.CurrencyConverter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyConverterTest {

    @Test
    void testUsdToUsdConversion() {
        BigDecimal amount = new BigDecimal("100000.00");
        BigDecimal result = CurrencyConverter.toUsd(amount, Currency.USD);
        assertEquals(new BigDecimal("100000.00"), result);
    }

    @Test
    void testInrToUsdConversion() {
        BigDecimal amount = new BigDecimal("1000000.00");
        BigDecimal result = CurrencyConverter.toUsd(amount, Currency.INR);
        assertEquals(new BigDecimal("12000.00"), result);
    }

    @Test
    void testEurToUsdConversion() {
        BigDecimal amount = new BigDecimal("50000.00");
        BigDecimal result = CurrencyConverter.toUsd(amount, Currency.EUR);
        assertEquals(new BigDecimal("54000.00"), result);
    }

    @Test
    void testGbpToUsdConversion() {
        BigDecimal amount = new BigDecimal("50000.00");
        BigDecimal result = CurrencyConverter.toUsd(amount, Currency.GBP);
        assertEquals(new BigDecimal("63500.00"), result);
    }

    @Test
    void testNullAmountOrCurrencyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.toUsd(null, Currency.USD));
        assertThrows(IllegalArgumentException.class, () -> CurrencyConverter.toUsd(BigDecimal.TEN, null));
    }
}
