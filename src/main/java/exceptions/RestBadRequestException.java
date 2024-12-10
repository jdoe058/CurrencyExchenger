package exceptions;

import jakarta.servlet.http.HttpServletResponse;

public class RestBadRequestException extends RestException {
    public RestBadRequestException(String message) {
        super(message);
    }

    @Override
    public int getResponseMessageStatus() {
        return HttpServletResponse.SC_BAD_REQUEST;
    }
}
