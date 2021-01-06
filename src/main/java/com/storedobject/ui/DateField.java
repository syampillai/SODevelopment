package com.storedobject.ui;

import com.storedobject.core.DateUtility;

import java.sql.Date;

public class DateField extends com.storedobject.vaadin.DateField {

    public DateField() {
    }

    public DateField(String label) {
        super(label);
    }

    public DateField(Date initialValue) {
        super(initialValue);
    }

    public DateField(String label, Date initialValue) {
        super(label, initialValue);
    }

    @Override
    public Date getValue() {
        return DateUtility.create(super.getValue());
    }
}
