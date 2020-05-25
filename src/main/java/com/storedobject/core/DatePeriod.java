package com.storedobject.core;

import com.storedobject.common.DateUtility;

import java.sql.Time;
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

	private DatePeriod() {
		super(null, null);
	}

	public TimePeriod getTimePeriod() {
		return new TimePeriod((Time) null, null);
	}

	public static DatePeriod thisMonth() {
		return new DatePeriod();
	}

	public static DatePeriod monthTillToday() {
		return new DatePeriod();
	}

	public static DatePeriod monthTillYesterday() {
		return new DatePeriod();
	}

	public static DatePeriod thisYear() {
		return new DatePeriod();
	}

	public static DatePeriod yearTillToday() {
		return new DatePeriod();
	}

	public static DatePeriod yearTillYesterday() {
		return new DatePeriod();
	}

	public static DatePeriod tillToday(int fromDays) {
		return new DatePeriod();
	}

	public static DatePeriod tillYesterday(int fromDays) {
		return new DatePeriod();
	}
}