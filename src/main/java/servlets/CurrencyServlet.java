package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import exceptions.CurrencyMissingCodeException;
import exceptions.RestNotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final String CURRENCY_NOT_FOUND_MESSAGE = "Валюта не найдена";
    private static final CurrencyDao dao = CurrencyDao.getInstance();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        var currencyCode = getCurrencyCodeFromPath(req.getPathInfo())
                .orElseThrow(CurrencyMissingCodeException::new);
        var currency = dao.find(List.of(currencyCode));
        if (currency.isEmpty()) {
            throw new RestNotFoundException(CURRENCY_NOT_FOUND_MESSAGE);

        }
        mapper.writeValue(resp.getWriter(), currency.getFirst());
    }

    private Optional<String> getCurrencyCodeFromPath(String path) {
        if (path == null || !path.matches("^/[A-Za-z]{3}$")) {
            return Optional.empty();
        }
        return Optional.of(path.substring(1).toUpperCase());
    }
}
