package com.storedobject.core;

import java.sql.Date;
import java.util.Calendar;

public class DatePeriod extends AbstractPeriod<java.sql.Date> {

    public DatePeriod(Calendar from, Calendar to) {
    	super(DateUtility.create(from), DateUtility.create(to));
    }

    public DatePeriod(java.util.Date from, java.util.Date to) {
    	super(DateUtility.create(from), DateUtility.create(to));
    }

    public DatePeriod(java.sql.Date from, java.sql.Date to) {
    	super(from, to);
    }

	public static DatePeriod create(Date dateFrom, Date dateTo) {
		return dateFrom.after(dateTo) ? new DatePeriod(dateTo, dateFrom) : new DatePeriod(dateFrom, dateTo);
	}

	public static DatePeriod create(Date date) {
		return create(date, date);
	}

	public static DatePeriod create() {
		return create(DateUtility.today());
	}

	@Override
	protected java.sql.Date clone(java.sql.Date date) {
		if(date == null) {
			return DateUtility.today();
		}
		return DateUtility.create(date);
	}

	@Override
	protected boolean same(java.sql.Date one, java.sql.Date two) {
		return DateUtility.isSameDate(one, two);
	}

	@Override
	protected String toString(java.sql.Date date) {
		return DateUtility.formatDate(date);
	}

    public TimePeriod getTimePeriod() {
		return new TimePeriod(DateUtility.startTime(getFrom()), DateUtility.endTime(getTo()));
	}
	
	public static DatePeriod thisMonth() {
		java.sql.Date today = DateUtility.today();
		return new DatePeriod(DateUtility.startOfMonth(today), DateUtility.endOfMonth(today));
	}
	
	public static DatePeriod monthTillToday() {
		java.sql.Date today = DateUtility.today();
		return new DatePeriod(DateUtility.startOfMonth(today), today);
	}
	
	public static DatePeriod monthTillYesterday() {
		return new DatePeriod(DateUtility.startOfMonth(DateUtility.today()), DateUtility.yesterday());
	}
	
	public static DatePeriod thisYear() {
		java.sql.Date today = DateUtility.today();
		return new DatePeriod(DateUtility.startOfYear(today), DateUtility.endOfYear(today));
	}
	
	public static DatePeriod yearTillToday() {
		java.sql.Date today = DateUtility.today();
		return new DatePeriod(DateUtility.startOfYear(today), today);
	}
	
	public static DatePeriod yearTillYesterday() {
		return new DatePeriod(DateUtility.startOfYear(DateUtility.today()), DateUtility.yesterday());
	}

	public static DatePeriod tillToday(int fromDays) {
		java.sql.Date today = DateUtility.today();
		return new DatePeriod(DateUtility.addDay(today, -fromDays), today);
	}

	public static DatePeriod tillYesterday(int fromDays) {
    	java.sql.Date yesterday = DateUtility.yesterday();
		return new DatePeriod(DateUtility.addDay(yesterday, -fromDays), yesterday);
	}
}