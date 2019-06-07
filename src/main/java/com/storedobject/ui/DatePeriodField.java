package com.storedobject.ui;

import com.storedobject.core.DatePeriod;
import com.storedobject.vaadin.DateField;

import java.sql.Date;

public class DatePeriodField extends RangeField<DatePeriod, Date> {

    public DatePeriodField() {
        this(null, null);
    }

    public DatePeriodField(String label) {
        this(label, null);
    }

    public DatePeriodField(String label, DatePeriod initialValue) {
        super((x) -> new DateField(), (x) -> new DateField());
    }

    @Override
    protected DatePeriod create(Date from, Date to) {
        return new DatePeriod(from, to);
    }
}