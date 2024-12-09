package exeptions;

public class NotFoundException extends ErrorException {
    public NotFoundException(String message) {
        super(message);
    }
}
