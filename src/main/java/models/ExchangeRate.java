package models;

import lombok.*;

import java.math.BigDecimal;

@Value
@Builder
public class ExchangeRate {
    Integer id;
    Currency baseCurrency;
    Currency targetCurrency;
    BigDecimal rate;

    public String getBaseCurrencyCode() {
        return baseCurrency.getCode();
    }

    public String getTargetCurrencyCode() {
        return targetCurrency.getCode();
    }
}
