package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import exceptions.CurrencyNotFormFieldException;
import exceptions.ErrorException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import models.Currency;

import java.io.IOException;

import java.util.Map;
import java.util.Optional;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyDao dao = CurrencyDao.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        mapper.writeValue(resp.getWriter(), dao.findAll());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        System.out.println();
        Currency currency = getInputParameters(req).orElseThrow(CurrencyNotFormFieldException::new);
        Currency newCurrency = dao.save(currency).orElseThrow(ErrorException::new);
        mapper.writeValue(resp.getWriter(), newCurrency);
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
