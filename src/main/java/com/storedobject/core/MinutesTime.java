package com.storedobject.core;

import com.storedobject.common.Displayable;

import java.sql.Time;

/**
 * Represents a {@link Time} instance rounded to the nearest minute.
 * This class is an extension of {@code Time} and implements
 * the {@code Displayable} interface, ensuring that the time component
 * is always precise to the minute.
 * <p></p>
 * The primary purpose of this class is to provide accurate time precision
 * in minutes by truncating seconds, offering a string
 * representation formatted to show hours and minutes.
 *
 * @author Syam
 */
public class MinutesTime extends Time implements Displayable {

    /**
     * Constructs a new {@code MinutesTime} object, initializing it with the
     * current system time rounded to the nearest minute.
     * This constructor leverages the no-argument constructor to fetch
     * the current system time in milliseconds and ensures that the time
     * value is truncated to the minute precision by stripping the seconds
     * and milliseconds components.
     */
    public MinutesTime() {
        this(System.currentTimeMillis());
    }

    /**
     * Constructs a {@code MinutesTime} instance with the specified time in milliseconds.
     * The provided time is rounded down to the nearest minute by truncating the
     * seconds and milliseconds components.
     *
     * @param time the time in milliseconds, representing the number of milliseconds
     *             since the Unix epoch (January 1, 1970, 00:00:00 GMT).
     */
    public MinutesTime(long time) {
        super(time);
        setTime(time);
    }

    @Override
    public final void setTime(long time) {
        super.setTime(strip(time));
    }

    @Override
    public long getTime() {
        return strip(super.getTime());
    }

    private static long strip(long time) {
        return (time / 60000) * 60000;
    }

    @Override
    public String toDisplay() {
        return toString();
    }

    @Override
    public String toString() {
        return DateUtility.formatHHMM(this);
    }
}
