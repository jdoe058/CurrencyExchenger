package exceptions;

public class CurrencyAlreadyExistsException extends RestConflictException {
    public CurrencyAlreadyExistsException() {
        super("Валюта с таким кодом уже существует ");
    }
}
