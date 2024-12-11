package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ExchangeRateDao;
import exceptions.ExchangeRateMissingFieldException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateDao dao = ExchangeRateDao.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        mapper.writeValue(resp.getWriter(), dao.find());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRate preparedRate = getExchangeRateFromRequest(req)
                .orElseThrow(ExchangeRateMissingFieldException::new);
        ExchangeRate rate = dao.save(preparedRate);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        mapper.writeValue(resp.getWriter(), rate);
    }

    private Optional<ExchangeRate> getExchangeRateFromRequest(HttpServletRequest req) {
        CurrencyCode baseCurrencyCode = CurrencyCode.of(req.getParameter("baseCurrencyCode"));
        CurrencyCode targetCurrencyCode = CurrencyCode.of(req.getParameter("targetCurrencyCode"));
        String rate = req.getParameter("rate");

        if (rate == null) {
            return Optional.empty();
        }

        return Optional.of(ExchangeRate.builder()
                .baseCurrency(Currency.builder().code(baseCurrencyCode).build())
                .targetCurrency(Currency.builder().code(targetCurrencyCode).build())
                .rate(BigDecimal.valueOf(Double.parseDouble(rate)))
                .build());
    }
}
