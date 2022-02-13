package com.storedobject.ui;

import com.storedobject.core.ComputedDate;
import com.storedobject.ui.util.ComputedField;
import com.storedobject.vaadin.DateField;

import java.sql.Date;

public class ComputedDateField extends ComputedField<ComputedDate, Date> {

    private static final ComputedDate EMPTY = new ComputedDate(null,false);

    public ComputedDateField() {
        this(null, null);
    }

    public ComputedDateField(String label) {
        this(label, null);
    }

    public ComputedDateField(ComputedDate value) {
        this(null, value);
    }

    public ComputedDateField(String label, ComputedDate value) {
        super(new DateField(), label, EMPTY, value);
    }

    @Override
    public ComputedDate getEmptyValue() {
        return EMPTY.clone();
    }
}
