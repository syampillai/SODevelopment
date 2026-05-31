package com.storedobject.core;

import java.sql.Date;
import java.util.Calendar;

/**
 * Representation of a period of time when the start and end points are {@link java.sql.Date}s.
 *
 * @author Syam
 */
public class DatePeriod extends AbstractPeriod<java.sql.Date> {

    /**
     * Create from {@link Calendar} objects.
     *
     * @param from The starting point of the period.
     * @param to   The ending point of the period.
     */
    public DatePeriod(Calendar from, Calendar to) {
    	super(DateUtility.create(from), DateUtility.create(to));
    }

    /**
     * Create from {@link java.util.Date} objects.
     *
     * @param from The starting point of the period.
     * @param to   The ending point of the period.
     */
    public DatePeriod(java.util.Date from, java.util.Date to) {
    	super(DateUtility.create(from), DateUtility.create(to));
    }

    /**
     * Create from {@link java.sql.Date} objects.
     *
     * @param from The starting point of the period.
     * @param to   The ending point of the period.
     */
    public DatePeriod(java.sql.Date from, java.sql.Date to) {
    	super(from, to);
    }

    /**
     * Create a period from two dates. The dates will be swapped if from date is after the to date.
     *
     * @param dateFrom The starting point of the period.
     * @param dateTo   The ending point of the period.
     * @return The date period created.
     */
	public static DatePeriod create(Date dateFrom, Date dateTo) {
		return dateFrom.after(dateTo) ? new DatePeriod(dateTo, dateFrom) : new DatePeriod(dateFrom, dateTo);
	}

    /**
     * Create a period for a single date (from and to will be the same).
     *
     * @param date The date.
     * @return The date period created.
     */
	public static DatePeriod create(Date date) {
		return create(date, date);
	}

    /**
     * Create a period for today (from and to will be today).
     *
     * @return The date period created.
     */
	public static DatePeriod create() {
		return create(DateUtility.today());
	}

    /**
     * Clones the given date.
     *
     * @param date The date to be cloned. If null, today's date is used.
     * @return The cloned date.
     */
	@Override
	protected java.sql.Date clone(java.sql.Date date) {
		if(date == null) {
			return DateUtility.today();
		}
		return DateUtility.create(date);
	}

    /**
     * Checks if two dates are the same.
     *
     * @param one The first date.
     * @param two The second date.
     * @return True if they are the same.
     */
	@Override
	protected boolean same(java.sql.Date one, java.sql.Date two) {
		return DateUtility.isSameDate(one, two);
	}

    /**
     * Converts the date to a string.
     *
     * @param date The date to be converted.
     * @return The formatted string.
     */
	@Override
	protected String toString(java.sql.Date date) {
		return DateUtility.formatDate(date);
	}

    /**
     * Get a {@link TimePeriod} for this date period.
     *
     * @return The time period.
     */
    public TimePeriod getTimePeriod() {
		return new TimePeriod(DateUtility.startTime(getFrom()), DateUtility.endTime(getTo()));
	}

    /**
     * Create a period for the current month.
     *
     * @return The date period for the current month.
     */
	public static DatePeriod thisMonth() {
		java.sql.Date today = DateUtility.today();
		return new DatePeriod(DateUtility.startOfMonth(today), DateUtility.endOfMonth(today));
	}

    /**
     * Create a period from the start of the current month till today.
     *
     * @return The date period.
     */
	public static DatePeriod monthTillToday() {
		java.sql.Date today = DateUtility.today();
		return new DatePeriod(DateUtility.startOfMonth(today), today);
	}

    /**
     * Create a period from the start of the current month till yesterday.
     *
     * @return The date period.
     */
	public static DatePeriod monthTillYesterday() {
		return new DatePeriod(DateUtility.startOfMonth(DateUtility.today()), DateUtility.yesterday());
	}

    /**
     * Create a period for the current year.
     *
     * @return The date period for the current year.
     */
	public static DatePeriod thisYear() {
		java.sql.Date today = DateUtility.today();
		return new DatePeriod(DateUtility.startOfYear(today), DateUtility.endOfYear(today));
	}

    /**
     * Create a period from the start of the current year till today.
     *
     * @return The date period.
     */
	public static DatePeriod yearTillToday() {
		java.sql.Date today = DateUtility.today();
		return new DatePeriod(DateUtility.startOfYear(today), today);
	}

    /**
     * Create a period from the start of the current year till yesterday.
     *
     * @return The date period.
     */
	public static DatePeriod yearTillYesterday() {
		return new DatePeriod(DateUtility.startOfYear(DateUtility.today()), DateUtility.yesterday());
	}

    /**
     * Create a period ending today and starting a specific number of days ago.
     *
     * @param fromDays Number of days ago to start.
     * @return The date period.
     */
	public static DatePeriod tillToday(int fromDays) {
		java.sql.Date today = DateUtility.today();
		return new DatePeriod(DateUtility.addDay(today, -fromDays), today);
	}

    /**
     * Create a period ending yesterday and starting a specific number of days ago.
     *
     * @param fromDays Number of days ago to start.
     * @return The date period.
     */
	public static DatePeriod tillYesterday(int fromDays) {
    	java.sql.Date yesterday = DateUtility.yesterday();
		return new DatePeriod(DateUtility.addDay(yesterday, -fromDays), yesterday);
	}
}