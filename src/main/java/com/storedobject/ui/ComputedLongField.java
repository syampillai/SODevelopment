package com.storedobject.ui;

import com.storedobject.core.ComputedLong;
import com.storedobject.ui.util.ComputedField;
import com.storedobject.vaadin.LongField;

public class ComputedLongField extends ComputedField<ComputedLong, Long> {

    public ComputedLongField() {
        this((String)null, 0);
    }

    public ComputedLongField(String label) {
        this(label, 0);
    }

    public ComputedLongField(Long value) {
        this(null, new ComputedLong(value));
    }

    public ComputedLongField(ComputedLong value) {
        this(null, value);
    }

    public ComputedLongField(String label, Long value) {
        this(label, new ComputedLong(value));
    }

    public ComputedLongField(int width) {
        this(null, 0L, width);
    }

    public ComputedLongField(String label, int width) {
        this(label, 0L, width);
    }

    public ComputedLongField(Long value, int width) {
        this(null, new ComputedLong(value), width);
    }

    public ComputedLongField(ComputedLong value, int width) {
        this(null, value, width);
    }

    public ComputedLongField(String label, Long value, int width) {
        this(label, new ComputedLong(value), width);
    }

    public ComputedLongField(String label, ComputedLong value) {
        this(label, value, 0);
    }

    public ComputedLongField(String label, ComputedLong value, int width) {
        super(new LongField(), label, value, new ComputedLong());
        ((LongField)getField()).setLength(width);
    }

    public void setValue(Long value) {
        setValue(new ComputedLong(value));
    }
}
