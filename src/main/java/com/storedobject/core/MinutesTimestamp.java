package com.storedobject.core;

import com.storedobject.common.Displayable;

import java.sql.Timestamp;

/**
 * Represents a timestamp rounded to the nearest minute.
 * This class is an extension of {@code Timestamp} and implements
 * the {@code Displayable} interface, ensuring that the time component
 * is always precise to the minute. Nanoseconds are always set to zero.
 * <p></p>
 * The primary purpose of this class is to provide accurate time precision
 * in minutes by truncating seconds and nanoseconds and offering a string
 * representation formatted to show hours and minutes.
 */
public class MinutesTimestamp extends Timestamp implements Displayable {

    /**
     * Constructs a {@code MinutesTimestamp} object initialized to the current
     * system time, rounded down to the nearest minute.
     * <p></p>
     * This default constructor uses the system's current time in milliseconds
     * to create a timestamp object while truncating the seconds and nanoseconds
     * to ensure minute-level precision.
     */
    public MinutesTimestamp() {
        this(System.currentTimeMillis());
    }
    
    /**
     * Constructs a {@code MinutesTimestamp} instance with the specified time in milliseconds.
     * The timestamp is rounded down to the nearest minute by truncating seconds and nanoseconds.
     * Nanoseconds are always set to zero.
     *
     * @param time the time in milliseconds since the epoch, from which the minute-precise
     *             timestamp will be derived.
     */
    public MinutesTimestamp(long time) {
        super(time);
        setTime(time);
    }

    @Override
    public String toString() {
        return DateUtility.formatWithTimeHHMM(this);
    }

    @Override
    public String toDisplay() {
        return toString();
    }

    @Override
    public final void setTime(long time) {
        super.setTime((time / 60000) * 60000);
        setNanos(0);
    }

    @Override
    public final void setNanos(int n) {
        super.setNanos(0);
    }
}
