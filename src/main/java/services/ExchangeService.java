package services;

import dao.ExchangeRateDao;
import dto.AmountedExchangeRateDto;
import dto.ExchangeRateDto;
import models.CurrencyCode;
import models.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeService {
    private static final int AMOUNT_OPERATION_SCALE = 6;
    private static final CurrencyCode CROSS_CURRENCY_CODE = new CurrencyCode("usd");
    private static final ExchangeService INSTANCE = new ExchangeService();
    private final ExchangeRateDao dao = ExchangeRateDao.getInstance();

    private ExchangeService() {
    }

    public static ExchangeService getInstance() {
        return INSTANCE;
    }

    public Optional<AmountedExchangeRateDto> findExchangeRate(ExchangeRateDto dto) {
        return getDirectExchangeRate(dto)
                .or(() -> getReverseExchangeRate(dto))
                .or(() -> getCrossExchangeRate(dto));
    }

    private Optional<AmountedExchangeRateDto> getDirectExchangeRate(ExchangeRateDto dto) {
        return getExchangeRate(dto.baseCode(), dto.targetCode()).map(exchangeRate -> {
            BigDecimal rate = exchangeRate.getRate();
            BigDecimal convertedAmount = dto.amount().multiply(rate);
            return new AmountedExchangeRateDto(
                    exchangeRate.getBaseCurrency(),
                    exchangeRate.getTargetCurrency(),
                    rate, dto.amount(), convertedAmount);
        });
    }

    private Optional<AmountedExchangeRateDto> getReverseExchangeRate(ExchangeRateDto dto) {
        return getExchangeRate(dto.targetCode(), dto.baseCode()).map(exchangeRate -> {
            BigDecimal rate = exchangeRate.getRate();
            BigDecimal reverseRate = BigDecimal.ONE.divide(rate, AMOUNT_OPERATION_SCALE, RoundingMode.HALF_UP);
            BigDecimal convertedAmount = dto.amount().divide(rate, AMOUNT_OPERATION_SCALE, RoundingMode.HALF_UP);
            return new AmountedExchangeRateDto(
                    exchangeRate.getTargetCurrency(),
                    exchangeRate.getBaseCurrency(),
                    reverseRate, dto.amount(), convertedAmount);
        });
    }

    private Optional<AmountedExchangeRateDto> getCrossExchangeRate(ExchangeRateDto dto) {
        Optional<ExchangeRate> crossBase = getExchangeRate(CROSS_CURRENCY_CODE, dto.baseCode());
        Optional<ExchangeRate> crossTarget = getExchangeRate(CROSS_CURRENCY_CODE, dto.targetCode());

        if (crossBase.isPresent() && crossTarget.isPresent()) {
            BigDecimal crossRate = crossBase.get().getRate()
                    .divide(crossTarget.get().getRate(), AMOUNT_OPERATION_SCALE, RoundingMode.HALF_UP);
            BigDecimal convertedAmount = dto.amount().multiply(crossRate);
            return Optional.of(new AmountedExchangeRateDto(
                    crossBase.get().getTargetCurrency(),
                    crossTarget.get().getTargetCurrency(),
                    crossRate, dto.amount(), convertedAmount));
        }
        return Optional.empty();
    }

    private Optional<ExchangeRate> getExchangeRate(CurrencyCode from, CurrencyCode to) {
        return dao.find(ExchangeRateDto.of(from, to)).stream().findAny();
    }
}
