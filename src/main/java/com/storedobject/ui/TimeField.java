package com.storedobject.ui;

import com.storedobject.vaadin.TranslatedField;

import java.sql.Time;

public class TimeField extends TranslatedField<Time, Integer> {

    public TimeField(TimeResolution resolution) {
        super(resolution == TimeResolution.SECONDS ? new SecondsField() : new MinutesField(),
                resolution == TimeResolution.SECONDS ? (f, t) -> null : (f, t) -> null,
                resolution == TimeResolution.SECONDS ? (f, t) -> 0 : (f, t) -> 0);
    }

    public TimeField() {
        this(TimeResolution.MINUTES);
    }

    public TimeField(String label) {
        this(label, null, null);
    }

    public TimeField(Time initialValue) {
        this(null, initialValue, null);
    }

    public TimeField(String label, Time initialValue) {
        this(label, initialValue, null);
    }

    public TimeField(String label, TimeResolution resolution) {
        this(label, null, resolution);
    }

    public TimeField(Time initialValue, TimeResolution resolution) {
        this(null, initialValue, resolution);
    }

    public TimeField(String label, Time initialValue, TimeResolution resolution) {
        this(resolution);
    }

    public TimeResolution getResolution() {
        return null;
    }
}