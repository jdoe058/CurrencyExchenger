package models;

import exceptions.InvalidCurrencyCodeException;
import lombok.Value;
import lombok.EqualsAndHashCode;

@Value
@EqualsAndHashCode
public class CurrencyCode {

    public static final String INVALID_CURRENCY_CODE_MESSAGE =
            "Currency code must be a non-null string with exactly three letters.";
    String code;

    public CurrencyCode(String code) {
        if (code == null || !code.matches("^[A-Za-z]{3}$")) {
            throw new InvalidCurrencyCodeException(INVALID_CURRENCY_CODE_MESSAGE);
        }
        this.code = code.toUpperCase();
    }

    public static CurrencyCode of(String code) {
        return new CurrencyCode(code);
    }
}
