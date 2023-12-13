package com.storedobject.core;

/**
 * Error logger.
 *
 * @author Syam
 */
@FunctionalInterface
public interface ErrorLogger {

    /**
     * Log something.
     *
     * @param anything Anything to log.
     */
    default void log(Object anything) {
        log(anything, null);
    }

    /**
     * Log something when an error is thrown.
     *
     * @param anything Anything to log.
     * @param error Error that happened now.
     */
    void log(Object anything, Throwable error);
}
