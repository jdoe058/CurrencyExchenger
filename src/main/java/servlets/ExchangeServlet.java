package servlets;

import models.*;
import exceptions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ExchangeRateDao;
import dto.ExchangeRateDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    // rate in ExchangeRate always not null and not zero
    // RestRequestException catches in "public class RestApiFilter implements Filter"

    private static final int AMOUNT_OPERATION_SCALE = 6;
    private static final String AMOUNT_IS_MISSING_MESSAGE = "Amount is missing";
    private static final String AMOUNT_CANNOT_BE_ZERO_MESSAGE = "Amount cannot be zero";
    private static final String EXCHANGE_RATE_NOT_FOUND_MESSAGE = "Exchange rate not found";
    private static final String INVALID_EXCHANGE_PARAMETERS_MESSAGE = "Invalid exchange parameters : ";
    private static final CurrencyCode CROSS_CURRENCY_CODE = new CurrencyCode("usd");

    private final ObjectMapper mapper = new ObjectMapper();
    private final ExchangeRateDao dao = ExchangeRateDao.getInstance();

    private CurrencyCode baseCode;
    private CurrencyCode targetCode;
    private BigDecimal amount;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareParametersFromRequest(req);

        AmountedExchangeRateDto amountedExchangeRateDto = getDirectExchangeRate()
                .or(this::getReverseExchangeRate)
                .or(this::getCrossExchangeRate)
                .orElseThrow(() -> new RestNotFoundException(EXCHANGE_RATE_NOT_FOUND_MESSAGE));

        mapper.writeValue(resp.getWriter(), amountedExchangeRateDto);
    }

    private void prepareParametersFromRequest(HttpServletRequest req) {
        try {
            baseCode = CurrencyCode.of(req.getParameter("from"));
            targetCode = CurrencyCode.of(req.getParameter("to"));
            String amountParam = req.getParameter("amount");
            if (amountParam == null || amountParam.trim().isEmpty()) {
                throw new NumberFormatException(AMOUNT_IS_MISSING_MESSAGE);
            }
            amount = new BigDecimal(amountParam);
            if (amount.compareTo(BigDecimal.ZERO) == 0) {
                throw new ArithmeticException(AMOUNT_CANNOT_BE_ZERO_MESSAGE);
            }
        } catch (NumberFormatException | InvalidCurrencyCodeException | ArithmeticException e) {
            throw new RestBadRequestException(INVALID_EXCHANGE_PARAMETERS_MESSAGE + e.getMessage());
        }
    }

    private Optional<AmountedExchangeRateDto> getDirectExchangeRate() {
        return getExchangeRate(baseCode, targetCode).map(exchangeRate -> {
            BigDecimal rate = exchangeRate.getRate();
            BigDecimal convertedAmount = amount.multiply(rate);
            return new AmountedExchangeRateDto(
                    exchangeRate.getBaseCurrency(),
                    exchangeRate.getTargetCurrency(),
                    rate, amount, convertedAmount);
        });
    }

    private Optional<AmountedExchangeRateDto> getReverseExchangeRate() {
        return getExchangeRate(targetCode, baseCode).map(exchangeRate -> {
            BigDecimal rate = exchangeRate.getRate();
            BigDecimal reverseRate = BigDecimal.ONE.divide(rate, AMOUNT_OPERATION_SCALE, RoundingMode.HALF_UP);
            BigDecimal convertedAmount = amount.divide(rate, AMOUNT_OPERATION_SCALE, RoundingMode.HALF_UP);
            return new AmountedExchangeRateDto(
                    exchangeRate.getTargetCurrency(),
                    exchangeRate.getBaseCurrency(),
                    reverseRate, amount, convertedAmount);
        });
    }

    private Optional<AmountedExchangeRateDto> getCrossExchangeRate() {
        Optional<ExchangeRate> crossBaseExchangeRates = getExchangeRate(CROSS_CURRENCY_CODE, baseCode);
        Optional<ExchangeRate> crossTargetExchangeRates = getExchangeRate(CROSS_CURRENCY_CODE, targetCode);

        if (crossBaseExchangeRates.isEmpty() || crossTargetExchangeRates.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal crossRate = crossBaseExchangeRates.get().getRate().
                divide(crossTargetExchangeRates.get().getRate(), AMOUNT_OPERATION_SCALE, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = amount.multiply(crossRate);
        return Optional.of(new AmountedExchangeRateDto(
                crossBaseExchangeRates.get().getTargetCurrency(),
                crossTargetExchangeRates.get().getTargetCurrency(),
                crossRate, amount, convertedAmount));
    }

    private Optional<ExchangeRate> getExchangeRate(CurrencyCode from, CurrencyCode to) {
        return dao.findByCodes(ExchangeRateDto.of(from, to)).stream().findAny();
    }

    record AmountedExchangeRateDto(
            Currency baseCurrency,
            Currency targetCurrency,
            BigDecimal rate,
            BigDecimal amount,
            BigDecimal convertedAmount) {
    }
}
