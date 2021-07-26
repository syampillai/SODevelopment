package com.storedobject.iot;

/**
 * Global instance that represents the current status of IOT Data.
 *
 * @author Syam
 */
public class Status {

    /**
     * Get the last time data was updated.
     *
     * @return Time.
     */
    public static long lastUpdated() {
        return System.currentTimeMillis();
    }
}
