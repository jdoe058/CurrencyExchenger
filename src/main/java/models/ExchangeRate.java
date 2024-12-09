package models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@JsonPropertyOrder({"id", "baseCurrency", "targetCurrency", "rate"})
public class ExchangeRate {
    private int id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
}
