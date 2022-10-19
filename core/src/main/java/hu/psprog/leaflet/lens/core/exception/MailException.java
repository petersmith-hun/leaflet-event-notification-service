package hu.psprog.leaflet.lens.core.exception;

/**
 * Generic mail request processing exception.
 *
 * @author Peter Smith
 */
public class MailException extends RuntimeException {

    public MailException(String message) {
        super(message);
    }

    public MailException(String message, Throwable cause) {
        super(message, cause);
    }
}
