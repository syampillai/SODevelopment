package com.storedobject.core;

import com.storedobject.common.Displayable;

import java.sql.Date;
import java.util.Calendar;

/**
 * Represents a specific year. This class extends {@code Date} and is designed to handle
 * date-related operations specifically at the year level, resetting all other components
 * (month, day, time, etc.) to default values.
 *
 * @author Syam
 */
public class Year extends Date implements Displayable {

    /**
     * Default constructor for the {@code Year} class.
     * Initializes a {@code Year} instance representing the current year by using the current date provided by {@code DateUtility.today()}.
     * The time components (month, day, hour, minute, second, millisecond) are stripped, leaving only the year component.
     */
    public Year() {
        this(DateUtility.today().getTime());
    }

    /**
     * Constructs a {@code Year} instance initialized to the specified time in milliseconds.
     * The time is processed to represent only the year component by resetting all other
     * date and time values (e.g., month, day, hour, minute, second, and millisecond) to
     * their default values.
     *
     * @param time the milliseconds since the Unix epoch (January 1, 1970, 00:00:00 GMT)
     *             representing the initial year to set for this {@code Year} instance.
     */
    public Year(long time) {
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
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    @Override
    public String toString() {
        return StringUtility.padLeft("" + DateUtility.getYear(this), 4, '0');
    }

    @Override
    public String toDisplay() {
        return toString();
    }
}
