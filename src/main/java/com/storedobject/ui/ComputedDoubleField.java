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

    public ComputedDoubleField(Double value) {
        this(null, new ComputedDouble(value));
    }

    public ComputedDoubleField(ComputedDouble value) {
        this(null, value);
    }

    public ComputedDoubleField(String label, Double value) {
        this(label, new ComputedDouble(value));
    }

    public ComputedDoubleField(int decimals) {
        this(null, 0.0, decimals);
    }

    public ComputedDoubleField(String label, int decimals) {
        this(label, 0.0, decimals);
    }

    public ComputedDoubleField(Double value, int decimals) {
        this(null, new ComputedDouble(value), decimals);
    }

    public ComputedDoubleField(ComputedDouble value, int decimals) {
        this(null, value, decimals);
    }

    public ComputedDoubleField(String label, Double value, int decimals) {
        this(label, new ComputedDouble(value), decimals);
    }

    public ComputedDoubleField(String label, ComputedDouble value) {
        this(label, value, 0);
    }

    public ComputedDoubleField(String label, ComputedDouble value, int decimals) {
        super(new DoubleField(), label, value, new ComputedDouble());
        ((DoubleField)getField()).setLength(decimals);
    }

    public ComputedDoubleField(String label, Double value, int width, int decimals) {
        this(label, new ComputedDouble(value), width, decimals);
    }

    public ComputedDoubleField(String label, ComputedDouble value, int width, int decimals) {
        super(new DoubleField(0.0, width, decimals), label, value, new ComputedDouble());
    }
}
