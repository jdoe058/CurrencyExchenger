package exceptions;

import jakarta.servlet.http.HttpServletResponse;

public class RestConflictException extends RestException {
    public RestConflictException(String message) {
        super(message);
    }

    @Override
    public int getResponseMessageStatus() {
        return HttpServletResponse.SC_CONFLICT;
    }
}
