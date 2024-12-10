package servlets;

import dao.CurrencyDao;
import models.Currency;
import exceptions.CurrencyMissingFieldException;
import exceptions.RestException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.io.IOException;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyDao dao = CurrencyDao.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        mapper.writeValue(resp.getWriter(), dao.find(List.of()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Currency preparedCurrency = getInputParameters(req).orElseThrow(CurrencyMissingFieldException::new);
        Currency currency = dao.save(preparedCurrency).orElseThrow(RestException::new);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        mapper.writeValue(resp.getWriter(), currency);
    }

    private Optional<Currency> getInputParameters(HttpServletRequest req) {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if (name == null || code == null || sign == null) {
            return Optional.empty();
        }

        return Optional.of(Currency.builder()
                .fullName(name)
                .code(code)
                .sign(sign)
                .build());
    }
}
