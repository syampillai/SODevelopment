package com.storedobject.core;

public class SOException extends com.storedobject.common.SOException {

    public SOException() {
    }

    public SOException(String message) {
        super(message);
    }

    public SOException(String message, Throwable cause) {
        super(message, cause);
    }
}