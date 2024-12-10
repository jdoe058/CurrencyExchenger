package exceptions;

public class CurrencyMissingCodeException extends RestBadRequestException {
    public CurrencyMissingCodeException() {
        super("Код валюты отсутствует в адресе");
    }
}
