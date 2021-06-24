package com.storedobject.ui;

import com.storedobject.core.DatePeriod;
import com.storedobject.core.DateUtility;

import java.sql.Date;

public class MonthPeriodField extends RangeField<DatePeriod, Date> {

    public MonthPeriodField() {
        this(null, null);
    }

    public MonthPeriodField(String label) {
        this(label, null);
    }

    public MonthPeriodField(String label, DatePeriod initialValue) {
        super(x -> new MonthField(), x -> new MonthEndField());
        if(initialValue != null) {
            setValue(initialValue);
        }
        setLabel(label);
    }

    public int getFromMonth() {
        return DateUtility.getMonth(getFrom());
    }

    public int getFromYear() {
        return DateUtility.getYear(getFrom());
    }

    public int getToMonth() {
        return DateUtility.getMonth(getTo());
    }

    public int getToYear() {
        return DateUtility.getYear(getTo());
    }

    public String getDBCondition() {
        return getValue().getDBCondition();
    }

    @Override
    protected DatePeriod create(Date from, Date to) {
        return new DatePeriod(from, to);
    }
}