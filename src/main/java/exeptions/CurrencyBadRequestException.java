package exeptions;

public class CurrencyBadRequestException extends BadRequestException {
    public CurrencyBadRequestException() {
        super("Код валюты отсутствует в адресе");
    }
}
