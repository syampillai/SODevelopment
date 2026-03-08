package com.storedobject.core;

import com.storedobject.common.Displayable;

import java.sql.Date;
import java.util.Calendar;

/**
 * Represents a specific month by extending the {@link Date} class. This class ensures that
 * the time is normalized to the first day of the month, with time components such as hours,
 * minutes, seconds, and milliseconds set to zero. It implements the {@link Displayable}
 * interface to provide a string representation suited for display purposes.
 *
 * @author Syam
 */
public class Month extends Date implements Displayable {

    /**
     * Default constructor for the Month class. Initializes the instance to represent
     * the current month with the time set to the first day of the month and all
     * time components (hours, minutes, seconds, and milliseconds) set to zero.
     */
    public Month() {
        this(DateUtility.today().getTime());
    }

    /**
     * Constructs a {@code Month} object that represents a specific month based on the
     * provided time in milliseconds. The time provided is normalized to the first day
     * of the month with time components (hours, minutes, seconds, milliseconds) set to zero.
     *
     * @param time the time in milliseconds since the epoch (January 1, 1970, 00:00:00 GMT)
     *             corresponding to the month to be represented
     */
    public Month(long time) {
        super(time);
        setTime(time);
    }

    @Override
    public final void setTime(long time) {
        super.setTime(strip(time));
    }

    public final long getTime() {
        return strip(super.getTime());
    }

    private static long strip(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    @Override
    public String toString() {
        return DateUtility.formatMonth(this);
    }

    @Override
    public String toDisplay() {
        return toString();
    }
}
