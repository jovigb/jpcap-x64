package jpcap.api;

public class CaptureException extends RuntimeException {
    public CaptureException() {
        super();
    }

    public CaptureException(String message) {
        super(message);
    }

    public CaptureException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptureException(Throwable cause) {
        super(cause);
    }

    protected CaptureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
