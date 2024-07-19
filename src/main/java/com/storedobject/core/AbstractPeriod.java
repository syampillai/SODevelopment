package com.storedobject.core;

import com.storedobject.common.DateUtility;
import com.storedobject.common.Range;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public abstract class AbstractPeriod<T extends java.util.Date> extends Range<T> {

    public AbstractPeriod(T from, T to) {
    	super(from, to);
    }

    public Calendar getCalendarFrom() {
    	GregorianCalendar c = new GregorianCalendar();
    	c.setTime(getFrom());
        return c;
    }

    public Calendar getCalendarTo() {
    	GregorianCalendar c = new GregorianCalendar();
    	c.setTime(getTo());
        return c;
    }

    @Override
	protected long value(T date) {
    	return date.getTime();
    }
    
    public int getPeriodInDays() {
    	return DateUtility.getPeriodInDays(getFrom(), getTo());
    }
    
    public int getPeriodInMonths() {
    	return DateUtility.getPeriodInMonths(getFrom(), getTo());
    }
    
	protected String toDBString(T date) {
		return Database.format(date);
	}
    
	public int getMonth() {
		return getMonthFrom();
	}
	
	public int getYear() {
		return getYearFrom();
	}
	
	public int getMonthFrom() {
		return DateUtility.getMonth(getFrom());
	}
	
	public int getYearFrom() {
		return DateUtility.getYear(getFrom());
	}
	
	public int getMonthTo() {
		return DateUtility.getMonth(getTo());
	}
	
	public int getYearTo() {
		return DateUtility.getYear(getTo());
	}

	public String getDBCondition() {
		return dbCond(toDBString(getFrom()), toDBString(getTo()));
	}

	public String getDBCondition(TransactionManager tm) {
		return dbCond(toDBString(tm.dateGMT(getFrom())), toDBString(tm.dateGMT(getTo())));
	}

	public String getDBTimeCondition() {
		Timestamp t1 = DateUtility.startTime(getFrom()), t2 = DateUtility.endTime(getTo());
		return dbCond(Database.format(t1), Database.format(t2));
	}

	public String getDBTimeCondition(TransactionManager tm) {
		Timestamp t1 = tm.dateGMT(DateUtility.startTime(getFrom())), t2 = tm.dateGMT(DateUtility.endTime(getTo()));
		return dbCond(Database.format(t1), Database.format(t2));
	}

	private String dbCond(String from, String to) {
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

	public boolean inside(long time) {
		return time >= from.getTime() && time <= to.getTime();
	}

	public boolean inside(Date date) {
		return inside(date.getTime());
	}

	public String toShortString() {
		String from = toString(getFrom()), to = toString(getTo());
		if(from.equals(to)) {
			return from;
		}
		return from + " - " + to;
	}
}

