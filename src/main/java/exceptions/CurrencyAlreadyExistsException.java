package exceptions;

public class CurrencyAlreadyExistsException extends ErrorException {
    public CurrencyAlreadyExistsException() {
        super("Валюта с таким кодом уже существует ");
    }
}
