package com.storedobject.core;

import com.storedobject.common.Range;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

public abstract class AbstractPeriod<T extends java.util.Date> extends Range<T> {

    public AbstractPeriod(T from, T to) {
    	super(from, to);
    }

    public Calendar getCalendarFrom() {
        return new GregorianCalendar();
    }

    public Calendar getCalendarTo() {
        return new GregorianCalendar();
    }

    @Override
	protected long value(T date) {
    	return 0;
    }
    
    public int getPeriodInDays() {
    	return 0;
    }
    
    public int getPeriodInMonths() {
    	return 0;
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
    
	protected String toDBString(T date) {
        return "";
	}

	public String getDBCondition() {
		return " BETWEEN '" + toDBString(getFrom()) + "' AND '" + toDBString(getTo()) + "' ";
	}

	public String getDBCondition(TransactionManager tm) {
		return " BETWEEN '" + toDBString(tm.dateGMT(getFrom())) + "' AND '" + toDBString(tm.dateGMT(getTo())) + "' ";
	}

	public String getDBTimeCondition() {
		Timestamp t1 = com.storedobject.common.DateUtility.startTime(getFrom()), t2 = com.storedobject.common.DateUtility.endTime(getTo());
		return " BETWEEN '" + Database.format(t1) + "' AND '" + Database.format(t2) + "' ";
	}

	public String getDBTimeCondition(TransactionManager tm) {
		Timestamp t1 = tm.dateGMT(com.storedobject.common.DateUtility.startTime(getFrom())), t2 = tm.dateGMT(com.storedobject.common.DateUtility.endTime(getTo()));
		return " BETWEEN '" + Database.format(t1) + "' AND '" + Database.format(t2) + "' ";
	}
}