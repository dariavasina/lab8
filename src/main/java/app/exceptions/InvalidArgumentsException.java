package app.exceptions;

public class InvalidArgumentsException extends Exception {
    public InvalidArgumentsException(String message) {
        super(message);
    }

    public InvalidArgumentsException() {
    }
}
