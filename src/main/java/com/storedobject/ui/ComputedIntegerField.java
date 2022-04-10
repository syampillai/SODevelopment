package com.storedobject.ui;

import com.storedobject.core.ComputedInteger;
import com.storedobject.ui.util.ComputedField;
import com.storedobject.vaadin.IntegerField;

public class ComputedIntegerField extends ComputedField<ComputedInteger, Integer> {

    public ComputedIntegerField() {
        this((String)null, 0);
    }

    public ComputedIntegerField(String label) {
        this(label, 0);
    }

    public ComputedIntegerField(Integer value) {
        this(null, new ComputedInteger(value));
    }

    public ComputedIntegerField(ComputedInteger value) {
        this(null, value);
    }

    public ComputedIntegerField(String label, Integer value) {
        this(label, new ComputedInteger(value));
    }

    public ComputedIntegerField(int width) {
        this(null, 0, width);
    }

    public ComputedIntegerField(String label, int width) {
        this(label, 0, width);
    }

    public ComputedIntegerField(Integer value, int width) {
        this(null, new ComputedInteger(value), width);
    }

    public ComputedIntegerField(ComputedInteger value, int width) {
        this(null, value, width);
    }

    public ComputedIntegerField(String label, Integer value, int width) {
        this(label, new ComputedInteger(value), width);
    }

    public ComputedIntegerField(String label, ComputedInteger value) {
        this(label, value, 0);
    }

    public ComputedIntegerField(String label, ComputedInteger value, int width) {
        super(new IntegerField(), label, value, new ComputedInteger());
        ((IntegerField)getField()).setLength(width);
    }

    public void setValue(Integer value) {
        setValue(new ComputedInteger(value));
    }
}
