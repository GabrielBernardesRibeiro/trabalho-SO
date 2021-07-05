package exceptions;

public class InvalidAddressException extends Exception {
    public InvalidAddressException(String error) {
        super(error);
    }
}