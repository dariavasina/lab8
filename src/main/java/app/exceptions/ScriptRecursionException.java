package app.exceptions;

public class ScriptRecursionException extends Exception{
    public ScriptRecursionException() {
    }

    public ScriptRecursionException(String message) {
        super(message);
    }
}
