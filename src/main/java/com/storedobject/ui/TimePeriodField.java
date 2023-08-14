package com.storedobject.ui;

import com.storedobject.core.DateUtility;
import com.storedobject.core.TimePeriod;

import java.sql.Time;

public class TimePeriodField extends RangeField<TimePeriod, Time> {

    public TimePeriodField() {
        this(null, null);
    }

    public TimePeriodField(String label) {
        this(label, null);
    }

    public TimePeriodField(String label, TimePeriod initialValue) {
        super((x) -> new TimeField(), (x) -> new TimeField());
        if(initialValue == null) {
            initialValue = new TimePeriod(DateUtility.startOfToday(), DateUtility.now());
        }
        setValue(initialValue);
        setPresentationValue(getValue());
        setLabel(label);
    }

    @Override
    protected TimePeriod create(Time from, Time to) {
        return new TimePeriod(from, to);
    }
}