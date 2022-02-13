package com.storedobject.ui;

import com.storedobject.core.DatePeriod;
import com.storedobject.core.DateUtility;
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
        if(initialValue == null) {
            initialValue = create(DateUtility.startOfMonth(), DateUtility.today());
        }
        if(initialValue != null) {
            setValue(initialValue);
        }
        setPresentationValue(getValue());
        setLabel(label);
    }

    @Override
    protected DatePeriod create(Date from, Date to) {
        return new DatePeriod(from, to);
    }

    /**
     * Epoch value to set. Epoch value determines how a 2 digit year value is interpreted. Epoch value is added to
     * the 2 digit year value. The default value of epoch is the first year of the century. For example, for the 21st
     * century, the default epoch value is 2000.
     *
     * @param epoch Epoch value to set.
     */
    public void setEpoch(int epoch) {
        ((DateField)getFromField()).setEpoch(epoch);
        ((DateField)getToField()).setEpoch(epoch);
    }

    /**
     * Get the current epoch value. (Please see {@link #setEpoch(int)}).
     *
     * @return Current the current epoch value.
     */
    public int getEpoch() {
        return ((DateField)getFromField()).getEpoch();
    }
}