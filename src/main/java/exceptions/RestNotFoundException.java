package exceptions;

import jakarta.servlet.http.HttpServletResponse;

public class RestNotFoundException extends RestException {
    public RestNotFoundException(String message) {
        super(message);
    }

    @Override
    public int getResponseMessageStatus() {
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
