package com.storedobject.ui;

import com.storedobject.core.DateUtility;
import com.storedobject.vaadin.CustomField;

import java.sql.Timestamp;

public class TimestampField extends CustomField<Timestamp> {

    public TimestampField() {
        this((TimeResolution)null);
    }

    public TimestampField(String label) {
        this(label, null, null);
    }

    public TimestampField(Timestamp initialValue) {
        this(null, initialValue, null);
    }

    public TimestampField(String label, Timestamp initialValue) {
        this(label, initialValue, null);
    }

    public TimestampField(TimeResolution resolution) {
        super(DateUtility.now());
    }

    public TimestampField(String label, TimeResolution resolution) {
        this(label, null, resolution);
    }

    public TimestampField(Timestamp initialValue, TimeResolution resolution) {
        this(null, initialValue, resolution);
    }

    public TimestampField(String label, Timestamp initialValue, TimeResolution resolution) {
        this(resolution);
    }

    public final TimeResolution getResolution() {
        return null;
    }

    @Override
    protected Timestamp generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(Timestamp value) {
    }
}