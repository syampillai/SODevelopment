package com.storedobject.core;

import com.storedobject.common.DateUtility;
import com.storedobject.common.Range;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * AbstractPeriod represents a time period defined by a start date (from)
 * and an end date (to). It extends the {@code Range} class and provides utility
 * methods for working with time periods. It supports operations such as
 * retrieving formatted date strings, calculating date-based conditions for databases,
 * and checking for overlaps or inclusion within the defined period.
 *
 * @param <T> The type of the date, extending {@link Date}.
 * @author Syam
 */
public abstract class AbstractPeriod<T extends java.util.Date> extends Range<T> {

	/**
	 * Constructs a new AbstractPeriod object with the specified start and end points.
	 *
	 * @param from The starting point of the period.
	 * @param to   The ending point of the period.
	 */
    public AbstractPeriod(T from, T to) {
    	super(from, to);
    }

	/**
	 * Retrieves a Calendar instance representing the starting point of the period.
	 * The date is extracted using the `getFrom` method and set into a GregorianCalendar object.
	 *
	 * @return A Calendar instance initialized to the starting date of the period.
	 */
    public Calendar getCalendarFrom() {
    	GregorianCalendar c = new GregorianCalendar();
    	c.setTime(getFrom());
        return c;
    }

	/**
	 * Retrieves a Calendar instance representing the ending point of the period.
	 * The date is extracted using the `getTo` method and set into a GregorianCalendar object.
	 *
	 * @return A Calendar instance initialized to the ending date of the period.
	 */
    public Calendar getCalendarTo() {
    	GregorianCalendar c = new GregorianCalendar();
    	c.setTime(getTo());
        return c;
    }

	/**
	 * Gets the long value representing the date.
	 *
	 * @param date Date to get the value for.
	 * @return Time in milliseconds.
	 */
    @Override
	protected long value(T date) {
    	return date.getTime();
    }

	/**
	 * Calculates the duration of the period in days by determining the difference
	 * between the start and end points of the period.
	 *
	 * @return The number of days in the current period.
	 */
	public int getPeriodInDays() {
    	return DateUtility.getPeriodInDays(getFrom(), getTo());
    }

	/**
	 * Calculates and retrieves the total duration of the period in months.
	 * The period is determined based on the start and end points, where the
	 * number of months between these points is calculated.
	 *
	 * @return The number of months within the period.
	 */
	public int getPeriodInMonths() {
    	return DateUtility.getPeriodInMonths(getFrom(), getTo());
    }

	/**
	 * Converts the given `date` to a database-compatible string format.
	 *
	 * @param date The date to be converted.
	 * @return A string representation of the date formatted according to database requirements.
	 */
	protected String toDBString(T date) {
		return Database.format(date);
	}

	/**
	 * Retrieves the month value of the starting point of the period.
	 *
	 * @return The integer representation of the month (1-based, where January is 1 and December is 12)
	 *         corresponding to the starting point of the period.
	 */
	public int getMonth() {
		return getMonthFrom();
	}

	/**
	 * Retrieves the year value of the starting point of the period.
	 *
	 * @return The integer representation of the year corresponding to the starting point of the period.
	 */
	public int getYear() {
		return getYearFrom();
	}

	/**
	 * Retrieves the month value of the starting point of the period.
	 * The month is extracted from the starting date using the {@code getFrom} method.
	 *
	 * @return The integer representation of the month (1-based, where January is 1 and December is 12)
	 *         corresponding to the starting point of the period.
	 */
	public int getMonthFrom() {
		return DateUtility.getMonth(getFrom());
	}

	/**
	 * Retrieves the year value from the starting point of the period.
	 * The year is extracted using the `getFrom` method and processed
	 * through the `DateUtility.getYear` method.
	 *
	 * @return The integer representation of the year corresponding to
	 *         the starting point of the period.
	 */
	public int getYearFrom() {
		return DateUtility.getYear(getFrom());
	}

	/**
	 * Retrieves the month value of the ending point of the period.
	 * The month is derived from the date returned by the {@code getTo} method using the {@code DateUtility.getMonth} utility.
	 *
	 * @return The integer representation of the month (1-based, where January is 1 and December is 12)
	 *         corresponding to the ending point of the period.
	 */
	public int getMonthTo() {
		return DateUtility.getMonth(getTo());
	}

	/**
	 * Retrieves the year value of the ending point of the period.
	 * The year is derived using the {@code getTo} method and processed through the {@code DateUtility.getYear} utility.
	 *
	 * @return The integer representation of the year corresponding to the ending point of the period.
	 */
	public int getYearTo() {
		return DateUtility.getYear(getTo());
	}

	/**
	 * Generates a database condition string representing a range between two date points.
	 *
	 * @return A string representing the database condition for the period. If the start or end
	 *         points are null, the method returns null. If the start and end points are equal,
	 *         the result is an equality condition. Otherwise, the result is a range condition
	 *         in the format "BETWEEN 'start' AND 'end'".
	 */
	public String getDBCondition() {
		return dbCond(toDBString(getFrom()), toDBString(getTo()));
	}

	/**
	 * Generates a database condition string for a date range based on the starting and ending points of the period.
	 * The dates are converted to a database-compatible string format using the `TransactionManager` and utility methods.
	 *
	 * @param tm The TransactionManager instance used for retrieving and formatting the date in GMT.
	 * @return A string representing the database condition for the period. The result is based on the formatted
	 *         start and end dates. If the start or end points are null, the method returns null.
	 *         If the start and end points are equal, the result is an equality condition.
	 *         Otherwise, the result represents a range condition in the format "BETWEEN 'start' AND 'end'".
	 */
	public String getDBCondition(TransactionManager tm) {
		return dbCond(toDBString(tm.dateGMT(getFrom())), toDBString(tm.dateGMT(getTo())));
	}

	/**
	 * Generates a database condition string representing the time range between the start of the 'from' date
	 * and the end of the 'to' date.
	 *
	 * @return A string representing the database condition for the time period.
	 */
	public String getDBTimeCondition() {
		Timestamp t1 = DateUtility.startTime(getFrom()), t2 = DateUtility.endTime(getTo());
		return dbCond(Database.format(t1), Database.format(t2));
	}

	/**
	 * Generates a database condition string for the time range (start of 'from' to end of 'to')
	 * using the specified TransactionManager to convert to GMT.
	 *
	 * @param tm The TransactionManager instance to use for GMT conversion.
	 * @return A string representing the database condition for the time period in GMT.
	 */
	public String getDBTimeCondition(TransactionManager tm) {
		Timestamp t1 = tm.dateGMT(DateUtility.startTime(getFrom())), t2 = tm.dateGMT(DateUtility.endTime(getTo()));
		return dbCond(Database.format(t1), Database.format(t2));
	}

	/**
	 * Generates a database condition string for the given from and to strings.
	 *
	 * @param from From string.
	 * @param to To string.
	 * @return Database condition string.
	 */
	private String dbCond(String from, String to) {
		if(from == null || to == null) {
			return null;
		}
		if(from.equals(to)) {
			return "='" + from + "'";
		}
		return " BETWEEN '" + from + "' AND '" + to + "'";
	}

	/**
	 * Convert to string in the given "date format". The "date format" passed must be a valid format for
	 * creating a formatted date/date-time with {@link SimpleDateFormat}. For example, by passing "MMM dd, yyyy" will
	 * result in a formatted output like "Jan 23, 1998 - Mar 6, 1999".
	 *
	 * @param format Date format.
	 * @return Formatted output.
	 */
	public String toString(String format) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(getFrom()) + " - " + f.format(getTo());
	}

	/**
	 * Checks if the given time is within this period.
	 *
	 * @param time Time in milliseconds to check.
	 * @return True if the time is within the period (inclusive of 'from', exclusive of 'to').
	 */
	public boolean inside(long time) {
		return time >= from.getTime() && time < to.getTime();
	}

	/**
	 * Checks if the given date is within this period.
	 *
	 * @param date Date to check.
	 * @return True if the date is within the period.
	 */
	public boolean inside(Date date) {
		return date != null && inside(date.getTime());
	}

	/**
	 * Returns a short string representation of the period. If start and end dates are the same,
	 * only one date is returned.
	 *
	 * @return Short string representation.
	 */
	public String toShortString() {
		String from = toString(getFrom()), to = toString(getTo());
		if(from.equals(to)) {
			return from;
		}
		return from + " - " + to;
	}

	/**
	 * Checks if this period overlaps with another period.
	 *
	 * @param other The other period to check against.
	 * @return True if the periods overlap.
	 */
	public boolean overlaps(AbstractPeriod<?> other) {
		if(other == null) {
			return false;
		}
		if(other.from.getTime() < from.getTime()) {
			return other.overlaps(this);
		}
		return inside(other.from);
	}
}

