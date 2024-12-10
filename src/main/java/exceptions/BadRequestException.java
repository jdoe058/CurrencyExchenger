package exceptions;

public class BadRequestException extends ErrorException {
    public BadRequestException(String message) {
        super(message);
    }
}
