package servlets;

import dto.AmountedExchangeRateDto;
import models.*;
import exceptions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRateDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.ExchangeService;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private static final String AMOUNT_IS_MISSING_MESSAGE = "Amount is missing";
    private static final String AMOUNT_CANNOT_BE_ZERO_MESSAGE = "Amount cannot be zero";
    private static final String EXCHANGE_RATE_NOT_FOUND_MESSAGE = "Exchange rate not found";
    private static final String INVALID_EXCHANGE_PARAMETERS_MESSAGE = "Invalid exchange parameters : ";

    private final ObjectMapper mapper = new ObjectMapper();
    private final ExchangeService service = ExchangeService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRateDto exchangeRateDto = prepareParametersFromRequest(req);

        AmountedExchangeRateDto amountedExchangeRateDto = service.findExchangeRate(exchangeRateDto)
                .orElseThrow(() -> new RestNotFoundException(EXCHANGE_RATE_NOT_FOUND_MESSAGE));

        mapper.writeValue(resp.getWriter(), amountedExchangeRateDto);
    }

    private ExchangeRateDto prepareParametersFromRequest(HttpServletRequest req) {
        try {
            CurrencyCode baseCode = CurrencyCode.of(req.getParameter("from"));
            CurrencyCode targetCode = CurrencyCode.of(req.getParameter("to"));
            String amountParam = req.getParameter("amount");
            if (amountParam == null || amountParam.trim().isEmpty()) {
                throw new NumberFormatException(AMOUNT_IS_MISSING_MESSAGE);
            }

            ExchangeRateDto exchangeRateDto = ExchangeRateDto.of(baseCode, targetCode, new BigDecimal(amountParam));
            if (!exchangeRateDto.isValidAmount()) {
                throw new ArithmeticException(AMOUNT_CANNOT_BE_ZERO_MESSAGE);
            }
            return exchangeRateDto;

        } catch (NumberFormatException | InvalidCurrencyCodeException | ArithmeticException e) {
            throw new RestBadRequestException(INVALID_EXCHANGE_PARAMETERS_MESSAGE + e.getMessage());
        }
    }
}
