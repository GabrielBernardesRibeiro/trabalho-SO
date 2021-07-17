package exceptions;

public class CorruptedFileException extends Exception {
    public CorruptedFileException(String error) {
        super(error);
    }
}