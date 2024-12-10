package exceptions;

public class ExchangeRateNotFoundException extends RestNotFoundException {
    public ExchangeRateNotFoundException() {
        super("Обменный курс для пары не найден");
    }
}
