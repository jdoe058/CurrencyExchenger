package exceptions;

public class ErrorException extends RuntimeException {
    public ErrorException() {
        super("Ошибка (например, база данных недоступна)");
    }

    public ErrorException(String message) {
        super(message);
    }

    public String jsonMessage() {
        return "{\"message\":\"%s\"}".formatted(getMessage());
    }
}
