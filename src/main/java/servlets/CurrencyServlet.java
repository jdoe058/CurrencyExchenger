package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import exeptions.CurrencyBadRequestException;
import exeptions.CurrencyNotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final CurrencyDao dao = CurrencyDao.getInstance();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        var currencyCode = getCurrencyCodeFromPath(req.getPathInfo())
                .orElseThrow(CurrencyBadRequestException::new);
        var currency = dao.findByCode(currencyCode)
                .orElseThrow(CurrencyNotFoundException::new);
        mapper.writeValue(resp.getWriter(), currency);
    }

    private Optional<String> getCurrencyCodeFromPath(String path) {
        //todo regex
        if (path == null || path.length() != 4) {
            return Optional.empty();
        }
        return Optional.of(path.substring(1).toUpperCase());
    }
}
