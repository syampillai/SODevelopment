package com.storedobject.ui;

import com.storedobject.core.ComputedMinute;
import com.storedobject.ui.util.ComputedField;

public class ComputedMinutesField extends ComputedField<ComputedMinute, Integer> {

    public ComputedMinutesField() {
        this((String) null, 0);
    }

    public ComputedMinutesField(String label) {
        this(label, 0);
    }

    public ComputedMinutesField(Integer value) {
        this(null, new ComputedMinute(value));
    }

    public ComputedMinutesField(ComputedMinute value) {
        this(null, value);
    }

    public ComputedMinutesField(String label, Integer value) {
        this(label, new ComputedMinute(value));
    }

    public ComputedMinutesField(int width) {
        this(null, 0, width);
    }

    public ComputedMinutesField(String label, int width) {
        this(label, 0, width);
    }

    public ComputedMinutesField(Integer value, int width) {
        this(null, new ComputedMinute(value), width);
    }

    public ComputedMinutesField(ComputedMinute value, int width) {
        this(null, value, width);
    }

    public ComputedMinutesField(String label, Integer value, int width) {
        this(label, new ComputedMinute(value), width);
    }

    public ComputedMinutesField(String label, ComputedMinute value) {
        this(label, value, 0);
    }

    public ComputedMinutesField(String label, ComputedMinute value, int width) {
        super(new MinutesField(), label, value, new ComputedMinute());
        getF().setLength(width);
    }

    public void setAllowDays(boolean allowDays) {
        getF().setAllowDays(allowDays);
    }

    public void setLength(int width) {
        getF().setLength(width);
    }

    private MinutesField getF() {
        return (MinutesField)getField();
    }
}