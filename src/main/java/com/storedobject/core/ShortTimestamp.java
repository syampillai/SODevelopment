package com.storedobject.core;

import com.storedobject.common.Displayable;

import java.sql.Timestamp;

/**
 * A subclass of {@link Timestamp} that represents a shortened timestamp format.
 * The timestamp is rounded to the nearest minute, and nanoseconds are set to zero.
 *
 * @author Syam
 */
public class ShortTimestamp extends Timestamp implements Displayable {

    /**
     * Constructs a ShortTimestamp instance initialized to the current system time.
     * The time is set to the nearest minute with nanoseconds truncated to zero.
     */
    public ShortTimestamp() {
        super(System.currentTimeMillis());
    }
    
    /**
     * Constructs a ShortTimestamp object with the specified time value.
     * The timestamp is internally rounded to the nearest minute, and nanoseconds
     * are set to zero.
     *
     * @param time the time value in milliseconds since the epoch.
     */
    public ShortTimestamp(long time) {
        super(time);
    }

    @Override
    public String toString() {
        return DateUtility.formatWithTimeHHMM(this);
    }

    @Override
    public String toDisplay() {
        return toString();
    }

    /**
     * Adjusts the time value of this {@code ShortTimestamp} object.
     * The time is rounded down to the nearest minute, and the nanoseconds
     * component is set to zero.
     *
     * @param time the time value in milliseconds since the epoch
     *             to be set for this {@code ShortTimestamp} object
     */
    @Override
    public void setTime(long time) {
        super.setTime((time / 60000) * 60000);
        setNanos(0);
    }
}
