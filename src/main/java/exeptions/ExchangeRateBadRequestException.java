package exeptions;

public class ExchangeRateBadRequestException extends BadRequestException {
    public ExchangeRateBadRequestException() {
        super("Коды валют пары отсутствуют в адресе");
    }
}
