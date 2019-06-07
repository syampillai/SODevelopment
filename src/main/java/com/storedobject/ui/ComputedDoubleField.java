package com.storedobject.ui;

import com.storedobject.core.ComputedDouble;
import com.storedobject.ui.util.ComputedField;
import com.storedobject.vaadin.DoubleField;

public class ComputedDoubleField extends ComputedField<ComputedDouble, Double> {

    public ComputedDoubleField() {
        this((String)null, 0);
    }

    public ComputedDoubleField(String label) {
        this(label, 0);
    }

    public ComputedDoubleField(Integer value) {
        this(null, new ComputedDouble(value));
    }

    public ComputedDoubleField(ComputedDouble value) {
        this(null, value);
    }

    public ComputedDoubleField(String label, Integer value) {
        this(label, new ComputedDouble(value));
    }

    public ComputedDoubleField(int width) {
        this(null, 0, width);
    }

    public ComputedDoubleField(String label, int width) {
        this(label, 0, width);
    }

    public ComputedDoubleField(Integer value, int width) {
        this(null, new ComputedDouble(value), width);
    }

    public ComputedDoubleField(ComputedDouble value, int width) {
        this(null, value, width);
    }

    public ComputedDoubleField(String label, Integer value, int width) {
        this(label, new ComputedDouble(value), width);
    }

    public ComputedDoubleField(String label, ComputedDouble value) {
        this(label, value, 0);
    }

    public ComputedDoubleField(String label, ComputedDouble value, int width) {
        super(new DoubleField(), label, value, new ComputedDouble());
        ((DoubleField)getField()).setLength(width);
    }
}
