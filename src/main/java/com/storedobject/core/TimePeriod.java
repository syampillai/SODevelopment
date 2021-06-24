package com.storedobject.core;

import java.util.Calendar;

public class TimePeriod extends AbstractPeriod<java.sql.Time> {

    public TimePeriod(Calendar from, Calendar to) {
        super(DateUtility.createTime(from), DateUtility.createTime(to));
    }

    public TimePeriod(java.util.Date from, java.util.Date to) {
        super(DateUtility.createTime(from), DateUtility.createTime(to));
    }

    public TimePeriod(java.sql.Time from, java.sql.Time to) {
        super(from, to);
    }
}