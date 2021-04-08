package craftout.exceptions;

public class LoadLevelException extends RuntimeException {

    public LoadLevelException(String fileName, Exception e) {
        super("Failed to load level: " + fileName, e);
    }

}
