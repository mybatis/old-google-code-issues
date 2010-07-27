package com.opensymphony.oscache.base;

public class NeedsRefreshException extends Exception {

    public NeedsRefreshException() {
        super();
    }

    public NeedsRefreshException(String message) {
        super(message);
    }

    public NeedsRefreshException(Throwable cause) {
        super(cause);
    }

    public NeedsRefreshException(String message, Throwable cause) {
        super(message, cause);
    }

}
