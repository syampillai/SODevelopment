package com.storedobject.core;

import java.util.Calendar;

/**
 * Representation of a period of time where the start and end points are {@link java.sql.Time}s.
 *
 * @author Syam
 */
public class TimePeriod extends AbstractPeriod<java.sql.Time> {

    /**
     * Create from {@link Calendar} objects.
     *
     * @param from The starting point of the period.
     * @param to   The ending point of the period.
     */
    public TimePeriod(Calendar from, Calendar to) {
    	super(DateUtility.createTime(from), DateUtility.createTime(to));
    }

    /**
     * Create from {@link java.util.Date} objects.
     *
     * @param from The starting point of the period.
     * @param to   The ending point of the period.
     */
    public TimePeriod(java.util.Date from, java.util.Date to) {
    	super(DateUtility.createTime(from), DateUtility.createTime(to));
    }

    /**
     * Create from {@link java.sql.Time} objects.
     *
     * @param from The starting point of the period.
     * @param to   The ending point of the period.
     */
    public TimePeriod(java.sql.Time from, java.sql.Time to) {
    	super(from, to);
    }

	/**
	 * Creates a {@link TimePeriod} instance using the provided start and end times.
	 * If the 'to' parameter occurs before the 'from' parameter, they are swapped to ensure
	 * the time period is valid.
	 *
	 * @param from The starting point of the period. Must not be null.
	 * @param to   The ending point of the period. Must not be null.
	 * @return A new {@link TimePeriod} instance representing the time range between 'from' and 'to'.
	 */
	public static TimePeriod create(java.sql.Time from, java.sql.Time to) {
		return to.after(from) ? new TimePeriod(from, to) : new TimePeriod(to, from);
	}

    /**
     * Clones the given time.
     *
     * @param data The time to be cloned. If null, the current time is used.
     * @return The cloned time.
     */
	@Override
	protected java.sql.Time clone(java.sql.Time data) {
		if(data == null) {
			return DateUtility.time();
		}
		return DateUtility.createTime(data);
	}

    /**
     * Checks if two times are the same.
     *
     * @param one The first time.
     * @param two The second time.
     * @return True if they are the same.
     */
	@Override
	protected boolean same(java.sql.Time one, java.sql.Time two) {
		return one.getTime() == two.getTime();
	}

    /**
     * Converts the time to a string.
     *
     * @param data The time to be converted.
     * @return The formatted string.
     */
	@Override
	protected String toString(java.sql.Time data) {
		return DateUtility.format(data);
	}

    /**
     * Converts the given time to a database-compatible string format.
     *
     * @param data The time to be converted.
     * @return A string representation of the time formatted according to database requirements.
     */
	@Override
	protected String toDBString(java.sql.Time data) {
		return Database.formatWithTime(data);
	}
}