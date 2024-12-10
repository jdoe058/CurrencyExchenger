package exceptions;

public class CurrencyNotFormFieldException extends BadRequestException {
    public CurrencyNotFormFieldException() {
        super("Отсутствует нужное поле формы");
    }
}
