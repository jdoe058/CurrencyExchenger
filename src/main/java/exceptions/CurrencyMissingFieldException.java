package exceptions;

public class CurrencyMissingFieldException extends RestBadRequestException {
    public CurrencyMissingFieldException() {
        super("Отсутствует нужное поле формы");
    }
}
