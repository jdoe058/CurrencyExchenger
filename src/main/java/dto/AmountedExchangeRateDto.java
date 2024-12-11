package dto;

import models.Currency;

import java.math.BigDecimal;

public record AmountedExchangeRateDto(
        Currency baseCurrency,
        Currency targetCurrency,
        BigDecimal rate,
        BigDecimal amount,
        BigDecimal convertedAmount) {
}