package exceptions;

public class CurrencyNotFoundException extends RestNotFoundException {
    public CurrencyNotFoundException() {
        super("Валюта не найдена");
    }
}
