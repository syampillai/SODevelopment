package com.storedobject.core;

import java.util.Calendar;
import java.util.Objects;

/**
 * Representation of a period of time where the start and end points are {@link java.sql.Timestamp}s.
 *
 * @author Syam
 */
public class TimestampPeriod extends AbstractPeriod<java.sql.Timestamp> {

    /**
     * Create from {@link Calendar} objects.
     *
     * @param from The starting point of the period.
     * @param to   The ending point of the period.
     */
    public TimestampPeriod(Calendar from, Calendar to) {
        super(DateUtility.createTimestamp(from.getTime()), DateUtility.createTimestamp(to.getTime()));
    }

    /**
     * Create from {@link java.util.Date} objects.
     *
     * @param from The starting point of the period.
     * @param to   The ending point of the period.
     */
    public TimestampPeriod(java.util.Date from, java.util.Date to) {
        super(DateUtility.createTimestamp(from), DateUtility.createTimestamp(to));
    }

    /**
     * Create from {@link java.sql.Timestamp} objects.
     *
     * @param from The starting point of the period.
     * @param to   The ending point of the period.
     */
    public TimestampPeriod(java.sql.Timestamp from, java.sql.Timestamp to) {
        super(from, to);
    }

    /**
     * Creates a {@link TimestampPeriod} object ensuring the order of the timestamps.
     * If the 'to' timestamp is before the 'from' timestamp, their order is reversed.
     *
     * @param from The starting point of the period. Must not be null.
     * @param to   The ending point of the period. Must not be null.
     * @return A {@link TimestampPeriod} object with 'from' as the earlier timestamp and 'to' as the later timestamp.
     */
    public static TimestampPeriod create(java.sql.Timestamp from, java.sql.Timestamp to) {
        return to.after(from) ? new TimestampPeriod(from, to) : new TimestampPeriod(to, from);
    }

    /**
     * Clones the given timestamp.
     *
     * @param data The timestamp to be cloned. If null, the current time is used.
     * @return The cloned timestamp.
     */
    @Override
    protected java.sql.Timestamp clone(java.sql.Timestamp data) {
        return DateUtility.createTimestamp(Objects.requireNonNullElseGet(data, DateUtility::time));
    }

    /**
     * Checks if two timestamps are the same.
     *
     * @param one The first timestamp.
     * @param two The second timestamp.
     * @return True if they are the same.
     */
    @Override
    protected boolean same(java.sql.Timestamp one, java.sql.Timestamp two) {
        return one.getTime() == two.getTime() && one.getNanos() == two.getNanos();
    }

    /**
     * Converts the timestamp to a string.
     *
     * @param data The timestamp to be converted.
     * @return The formatted string.
     */
    @Override
    protected String toString(java.sql.Timestamp data) {
        return DateUtility.format(data);
    }

    /**
     * Converts the given timestamp to a database-compatible string format.
     *
     * @param data The timestamp to be converted.
     * @return A string representation of the timestamp formatted according to database requirements.
     */
    @Override
    protected String toDBString(java.sql.Timestamp data) {
        return Database.formatWithTime(data);
    }
}