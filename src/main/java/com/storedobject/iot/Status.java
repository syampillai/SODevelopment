package com.storedobject.iot;

import java.sql.Timestamp;

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

    /**
     * Get the last time data was updated.
     *
     * @return Time.
     */
    public static Timestamp lastUpdatedAt() {
        return new Timestamp(lastUpdated());
    }
}
