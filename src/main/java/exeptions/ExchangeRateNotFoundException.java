package exeptions;

public class ExchangeRateNotFoundException extends NotFoundException {
    public ExchangeRateNotFoundException() {
        super("Обменный курс для пары не найден");
    }
}
