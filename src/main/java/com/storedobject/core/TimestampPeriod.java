package com.storedobject.core;

import java.util.Calendar;

public class TimestampPeriod extends AbstractPeriod<java.sql.Timestamp> {

    public TimestampPeriod(Calendar from, Calendar to) {
        super(DateUtility.createTimestamp(from.getTime()), DateUtility.createTimestamp(to.getTime()));
    }

    public TimestampPeriod(java.util.Date from, java.util.Date to) {
        super(DateUtility.createTimestamp(from), DateUtility.createTimestamp(to));
    }

    public TimestampPeriod(java.sql.Timestamp from, java.sql.Timestamp to) {
        super(from, to);
    }
}