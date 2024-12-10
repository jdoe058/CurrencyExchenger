package models;

import lombok.*;

import java.math.BigDecimal;

@Value
@Builder
public class ExchangeRate {
    int id;
    Currency baseCurrency;
    Currency targetCurrency;
    BigDecimal rate;
}
