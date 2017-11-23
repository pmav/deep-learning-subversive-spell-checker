package eu.pmav.dataset.exception;

public class DatabaseBuilderException extends Exception {

    public DatabaseBuilderException(String message) {
        super(message);
    }

    public DatabaseBuilderException(Throwable cause) {
        super(cause);
    }

    public DatabaseBuilderException(String message, Throwable cause) {
        super(message, cause);
    }
}
