package exception;

public class PolicyLoadException extends Exception {
    public PolicyLoadException(String message) {
        super(message);
    }

    public PolicyLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
