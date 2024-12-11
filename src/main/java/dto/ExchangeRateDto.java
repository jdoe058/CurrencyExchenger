package dto;

import models.CurrencyCode;

import java.math.BigDecimal;

public record ExchangeRateDto(CurrencyCode baseCode, CurrencyCode targetCode, BigDecimal amount) {
    public static ExchangeRateDto of(CurrencyCode baseCode, CurrencyCode targetCode) {
        return new ExchangeRateDto(baseCode, targetCode, null);
    }

    public boolean isValidAmount() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
