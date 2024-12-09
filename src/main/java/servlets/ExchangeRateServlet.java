package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ExchangeRateDao;
import dto.ExchangeRateFilterDto;
import exeptions.ExchangeRateBadRequestException;
import exeptions.ExchangeRateNotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRateDao dao = ExchangeRateDao.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        var currencyCodes = getCurrencyCodesFromPath(req.getPathInfo())
                .orElseThrow(ExchangeRateBadRequestException::new);
        var rate = dao.findByCodes(currencyCodes)
                .orElseThrow(ExchangeRateNotFoundException::new);
        mapper.writeValue(resp.getWriter(), rate);
    }

    private Optional<ExchangeRateFilterDto> getCurrencyCodesFromPath(String path) {
        if (path == null || !path.matches("^/[A-Za-z]{6}$")) {
            return Optional.empty();
        }
        String baseCode = path.substring(1, 4).toUpperCase();
        String targetCode = path.substring(4).toUpperCase();
        return Optional.of(new ExchangeRateFilterDto(baseCode, targetCode));
    }
}
