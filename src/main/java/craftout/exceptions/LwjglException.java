package craftout.exceptions;

public class LwjglException extends RuntimeException {

    public LwjglException(String message, Exception e) {
        super(message, e);
    }
}
