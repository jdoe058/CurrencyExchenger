package exceptions;

public class CurrencyNotFoundException extends NotFoundException {
    public CurrencyNotFoundException() {
        super("Валюта не найдена");
    }
}
