package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ExchangeRateDao;
import dto.ResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import models.ExchangeRate;

import java.util.Optional;

public class ExchangeRateService {
    private static final String BAD_REQUEST_MESSAGE = "Коды валют пары отсутствуют в адресе";
    private static final String NOT_FOUND_MESSAGE = "Обменный курс для пары не найден";
    private static final String INTERNAL_MESSAGE = "Ошибка (например, база данных недоступна)";
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();

    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final ErrorService errorService = ErrorService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ExchangeRateService() {
    }


    public ResponseDto findByCode(String code) throws JsonProcessingException {
        if (code == null || code.length() != 7) {
            return errorService.get(HttpServletResponse.SC_BAD_REQUEST, BAD_REQUEST_MESSAGE);
        }

        Optional<ExchangeRate> rate = Optional.empty();
        String baseCode = code.substring(1, 4).toUpperCase();
        String targetCode = code.substring(4).toUpperCase();

        try {
            rate = exchangeRateDao.findByCode(baseCode, targetCode);
        } catch (Exception e) {
            return errorService.get(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, INTERNAL_MESSAGE);
        }

        if (rate.isEmpty()) {
            return errorService.get(HttpServletResponse.SC_NOT_FOUND, NOT_FOUND_MESSAGE);
        }

        return new ResponseDto(HttpServletResponse.SC_OK, objectMapper.writeValueAsString(rate.get()));
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }
}
