package com.storedobject.ui;

import com.storedobject.core.DateUtility;
import com.storedobject.vaadin.TranslatedField;

import java.sql.Time;

public class TimeField extends TranslatedField<Time, Integer> {

    private static final long MILLIS = 1000L;
    private final TimeResolution resolution;

    public TimeField(TimeResolution resolution) {
        super(resolution == TimeResolution.SECONDS ? new SecondsField() : new MinutesField(),
                resolution == TimeResolution.SECONDS ? (f, t) -> toTimeFromSeconds(t) : (f, t) -> toTimeFromMinutes(t),
                resolution == TimeResolution.SECONDS ? (f, t) -> toSeconds(t) : (f, t) -> toMinutes(t));
        this.resolution = resolution == null ? TimeResolution.MINUTES : resolution;
        if(this.resolution == TimeResolution.SECONDS) {
            ((SecondsField)getField()).setTrimToDay(true);
        } else {
            ((MinutesField)getField()).setTrimToDay(true);
        }
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
        setLabel(label);
        setValue(initialValue);
    }

    @Override
    public void setValue(Time value) {
        super.setValue(value == null ? DateUtility.time() : value);
    }

    private static Time toTimeFromMinutes(int minutes) {
        return toTimeFromSeconds(minutes * 60);
    }

    private static Time toTimeFromSeconds(int seconds) {
        Time time = new Time(0);
        time.setTime(time.getTime() + (seconds * MILLIS));
        return time;
    }

    private static int toSeconds(Time time) {
        return (int) ((time.getTime() - DateUtility.trimHours(time).getTime()) / MILLIS);
    }

    private static int toMinutes(Time time) {
        return toSeconds(time) / 60;
    }

    public TimeResolution getResolution() {
        return resolution;
    }
}