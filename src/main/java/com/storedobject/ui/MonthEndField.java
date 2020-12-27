package com.storedobject.ui;

import com.storedobject.core.DatePeriod;
import com.storedobject.core.DateUtility;

import java.sql.Date;

public class MonthEndField extends MonthField {

    public MonthEndField() {
        this(null);
    }

    public MonthEndField(String label) {
        super(label);
    }

    @Override
    public void setValue(Date value) {
        super.setValue(DateUtility.endOfMonth(value));
    }

    @Override
    public DatePeriod getPeriod() {
        Date v = getValue();
        return new DatePeriod(DateUtility.startOfMonth(v), v);
    }
}
