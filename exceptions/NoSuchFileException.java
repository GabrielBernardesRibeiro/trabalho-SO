package exceptions;

public class NoSuchFileException extends Exception {
    public NoSuchFileException(String error) {
        super(error);
    }
}