package exceptions;

public class ExchangeRateMissingCodesException extends RestBadRequestException {
    public ExchangeRateMissingCodesException() {
        super("Коды валют пары отсутствуют в адресе");
    }
}
