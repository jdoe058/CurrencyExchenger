package services;

import models.Currency;
import dao.CurrencyDao;
import dto.ResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private static final ErrorService errorService = ErrorService.getInstance();
    private static final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BAD_REQUEST_MESSAGE = "Код валюты отсутствует в адресе";
    private static final String NOT_FOUND_MESSAGE = "Валюта не найдена";
    private static final String INTERNAL_MESSAGE = "Ошибка (например, база данных недоступна)";

    private CurrencyService() {
    }

    public ResponseDto findByCode(String code) throws JsonProcessingException {

        if (code == null || code.equals("/")) {
            return errorService.get(HttpServletResponse.SC_BAD_REQUEST, BAD_REQUEST_MESSAGE);
        }

        Optional<Currency> currency;
        code = code.substring(1).toUpperCase();

        try {
            currency = currencyDao.findByCode(code);
        } catch (Exception e) {
            return errorService.get(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, INTERNAL_MESSAGE);
        }

        if (currency.isEmpty()) {
            return errorService.get(HttpServletResponse.SC_NOT_FOUND, NOT_FOUND_MESSAGE);
        }

        return new ResponseDto(HttpServletResponse.SC_OK, objectMapper.writeValueAsString(currency.get()));
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }
}
