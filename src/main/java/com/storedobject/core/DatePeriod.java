package com.storedobject.core;

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

	@Override
	protected java.sql.Date clone(java.sql.Date date) {
		return null;
	}

	@Override
	protected boolean same(java.sql.Date one, java.sql.Date two) {
		return false;
	}

	@Override
	protected String toString(java.sql.Date date) {
		return null;
	}

	@Override
	protected String toDBString(java.sql.Date date) {
		return null;
	}
	
	public TimePeriod getTimePeriod() {
		return null;
	}
	
	public static DatePeriod thisMonth() {
		return null;
	}
	
	public static DatePeriod monthTillToday() {
		return null;
	}
	
	public static DatePeriod monthTillYesterday() {
		return null;
	}
	
	public static DatePeriod thisYear() {
		return null;
	}
	
	public static DatePeriod yearTillToday() {
		return null;
	}
	
	public static DatePeriod yearTillYesterday() {
		return null;
	}
}