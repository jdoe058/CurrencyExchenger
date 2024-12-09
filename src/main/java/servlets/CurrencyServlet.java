package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import exeptions.BadRequestException;
import exeptions.NotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Currency;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final String BAD_REQUEST_MESSAGE = "Код валюты отсутствует в адресе";
    private static final String NOT_FOUND_MESSAGE = "Валюта не найдена";
    private static final CurrencyDao dao = CurrencyDao.getInstance();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String currencyCode = req.getPathInfo();

        if (currencyCode == null || currencyCode.equals("/")) {
            throw new BadRequestException(BAD_REQUEST_MESSAGE);
        }

        Optional<Currency> currency;

        try {
            currency = dao.findByCode(currencyCode.substring(1).toUpperCase());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (currency.isEmpty()) {
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }

        mapper.writeValue(resp.getWriter(), currency.get());
    }
}
