package com.storedobject.core;

import com.storedobject.common.Range;

import java.util.Calendar;

public abstract class AbstractPeriod<T extends java.util.Date> extends Range<T> {

    public AbstractPeriod(T from, T to) {
    	super(from, to);
    }

    public Calendar getCalendarFrom() {
        return null;
    }

    public Calendar getCalendarTo() {
        return null;
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
        return null;
	}
    
    public String getDBCondition() {
        return null;
    }
    
    public String getDBTimeCondition() {
        return null;
    }
}