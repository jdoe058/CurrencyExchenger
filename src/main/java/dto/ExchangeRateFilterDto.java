package dto;

import models.CurrencyCode;

public record ExchangeRateFilterDto(CurrencyCode baseCode, CurrencyCode targetCode) {
}
