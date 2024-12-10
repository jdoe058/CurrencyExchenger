package exceptions;

import jakarta.servlet.http.HttpServletResponse;

public class RestException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Ошибка (например, база данных недоступна)";

    public RestException() {
        super(DEFAULT_MESSAGE);
    }

    public RestException(String message) {
        super(message);
    }

    public int getResponseMessageStatus() {
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    public String getResponseMessageBody() {
        return "{\"message\":\"%s\"}".formatted(getMessage());
    }
}
