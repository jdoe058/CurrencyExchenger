package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ExchangeRateDao;
import dto.ExchangeRateFilterDto;
import exceptions.InvalidCurrencyCodeException;
import exceptions.RestBadRequestException;
import exceptions.RestNotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.CurrencyCode;
import models.ExchangeRate;

import java.io.IOException;
import java.util.NoSuchElementException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    public static final int TARGET_CODE_BEGIN_INDEX_IN_PATH = 4;
    public static final int BASE_CODE_BEGIN_INDEX_IN_PATH = 1;
    public static final String CURRENCY_CODES_MISSED_MESSAGE = "Коды валют пары отсутствуют в адресе";
    public static final String EXCHANGE_RATE_NOT_FOUND_MESSAGE = "Обменный курс для пары не найден";
    private final ExchangeRateDao dao = ExchangeRateDao.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ExchangeRateFilterDto currencyCodes = getCurrencyCodesFromPath(req.getPathInfo());
            ExchangeRate rate = dao.findByCodes(currencyCodes).getFirst();
            mapper.writeValue(resp.getWriter(), rate);
        } catch (NoSuchElementException e) {
            throw new RestNotFoundException(EXCHANGE_RATE_NOT_FOUND_MESSAGE);
        } catch (InvalidCurrencyCodeException | IndexOutOfBoundsException e) {
            throw new RestBadRequestException(CURRENCY_CODES_MISSED_MESSAGE);
        }
    }

    private ExchangeRateFilterDto getCurrencyCodesFromPath(String path) {
        CurrencyCode baseCode = CurrencyCode.of(path.substring(
                BASE_CODE_BEGIN_INDEX_IN_PATH,
                TARGET_CODE_BEGIN_INDEX_IN_PATH));
        CurrencyCode  targetCode = CurrencyCode.of(path.substring(
                TARGET_CODE_BEGIN_INDEX_IN_PATH));
        return new ExchangeRateFilterDto(baseCode, targetCode);
    }
}
