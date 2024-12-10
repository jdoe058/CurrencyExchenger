package exceptions;

public class ExchangeRateMissingFieldException extends RestBadRequestException {
    public ExchangeRateMissingFieldException() {
        super("Отсутствует нужное поле формы");
    }
}
