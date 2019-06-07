package com.storedobject.ui;

import com.storedobject.core.DatePeriod;
import com.storedobject.core.DateUtility;
import com.storedobject.vaadin.CustomField;
import com.storedobject.vaadin.ListField;

import java.sql.Date;

public class MonthField extends CustomField<Date> {

    protected ListField<String> monthPart;
    protected ListField<Integer> yearPart;

    public MonthField() {
        this(null);
    }

    public MonthField(String label) {
        super(DateUtility.startOfMonth());
    }

    @Override
    public void setValue(Date value) {
        super.setValue(DateUtility.startOfMonth(value));
    }

    public void setYearRange(int yearOffset) {
        setYearRange(0, yearOffset);
    }

    public int getMonth() {
        return DateUtility.getMonth(getValue());
    }

    public int getYear() {
        return DateUtility.getYear(getValue());
    }

    public DatePeriod getPeriod() {
        return null;
    }

    public String getDBCondition() {
        return getPeriod().getDBCondition();
    }

    @Override
    protected Date generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(Date value) {
    }

    public void setYearRange(int yearFrom, int yearTo) {
    }
}
